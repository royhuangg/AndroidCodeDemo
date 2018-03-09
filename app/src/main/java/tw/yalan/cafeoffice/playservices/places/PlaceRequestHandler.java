package tw.yalan.cafeoffice.playservices.places;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import tw.yalan.cafeoffice.Config;


public class PlaceRequestHandler extends RequestHandler {
    private final static String SCHEME = "place";
    final GoogleApiClient googleApiClient;

    public PlaceRequestHandler(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }


    @Override
    public boolean canHandleRequest(Request data) {
        if (data.uri.getScheme() == null) {
            return false;
        }
        return data.uri.getScheme().equals(SCHEME);
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {
        Uri uri = request.uri;

        String name = uri.getHost();
        Integer width = Integer.valueOf(uri.getQueryParameter("width"));
        Integer height = Integer.valueOf(uri.getQueryParameter("height"));
        String indexParam = uri.getQueryParameter("index");
        Integer index = indexParam != null ? Integer.valueOf(indexParam) : 0;

        PendingResult<AutocompletePredictionBuffer> predictions = Places.GeoDataApi.getAutocompletePredictions(googleApiClient, name, null, null);
        AutocompletePredictionBuffer autocompletePredictions = predictions.await(10, TimeUnit.SECONDS);
        com.google.android.gms.common.api.Status status = autocompletePredictions.getStatus();
        if (!status.isSuccess()) {
            Config.loge("Error contacting API: " + status.toString());
            Config.loge("Error getting autocomplete prediction API call: " + status.toString());
            autocompletePredictions.release();
            return null;
        } else {
            Config.logi("Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");
            if (autocompletePredictions.getCount() == 0) {
                autocompletePredictions.release();
                return null;
            }
            AutocompletePrediction autocompletePrediction = autocompletePredictions.get(0);

            String placeId = autocompletePrediction.getPlaceId();
            autocompletePredictions.release();

            Config.loge("placeId:" + placeId);

            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(googleApiClient, placeId).await();
            Bitmap image = null;

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0) {

                    // Get the first bitmap and its attributions.
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(photoMetadataBuffer.getCount() - 1 > index ? index : photoMetadataBuffer.getCount() - 1);
                    CharSequence attribution = photo.getAttributions();
                    // Load a scaled bitmap for this photo.
                    image = photo.getScaledPhoto(googleApiClient, width, height).await()
                            .getBitmap();

                }

                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }
            if (image != null) {
                return new Result(image, Picasso.LoadedFrom.NETWORK);
            } else {
                return null;
            }
        }
    }
}
