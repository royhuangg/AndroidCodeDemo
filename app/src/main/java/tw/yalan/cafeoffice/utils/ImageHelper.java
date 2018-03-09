package tw.yalan.cafeoffice.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by Alan Ding on 2017/5/6.
 */

public class ImageHelper {

    public static void loadSquaredImage(Context context, String path, @DrawableRes int placeHolder, @DrawableRes int error, int strokeWidth, int cornerRadius, ImageView into) {
        if (path != null && !path.isEmpty()) {
            RequestBuilder<Drawable> builder;

            if (!path.startsWith("http")) {
                builder = Glide.with(context).load(Uri.fromFile(new File(path)));


            } else {
                builder = Glide.with(context).load(path);
            }

            RequestOptions options = new RequestOptions();
            options.centerCrop();
            if (placeHolder != 0) {
                options.placeholder(placeHolder);
            }
            if (error != 0) {
                options.error(error);
            }
            if (cornerRadius > 0) {
                options.transform(new RoundedCorners(cornerRadius));
            }

            builder.apply(options);

            builder.into(into);
        } else {
            if (error == 0) {
                return;
            }
            RequestOptions options = RequestOptions.centerCropTransform();

            if (cornerRadius > 0) {
                options.transform(new RoundedCorners(cornerRadius));
            }
            if (placeHolder != 0) {
                options.placeholder(placeHolder);
            }
            Glide.with(context)
                    .load(error)
                    .apply(options)
                    .into(into);
        }
    }


    public static void loadSquaredThumbnailImage(Context context,
                                                 String path,
                                                 @DrawableRes int placeHolder,
                                                 @DrawableRes int error,
                                                 int cornerRadius,
                                                 ImageView into) {
        if (path != null && !path.isEmpty()) {
            RequestBuilder<Drawable> builder;

            if (!path.startsWith("http")) {
                builder = Glide.with(context).load(Uri.fromFile(new File(path)));


            } else {
                builder = Glide.with(context).load(path);
            }
            RequestOptions options = RequestOptions.centerCropTransform().transform(new RoundedCorners(cornerRadius));


            if (placeHolder != 0) {
                options.placeholder(placeHolder);
            }
            if (error != 0) {
                options.error(error);
            }
            builder.thumbnail(0.3f).apply(options);

            builder.into(into);
        } else {
            if (error == 0) {
                return;
            }
            RequestOptions options = RequestOptions.centerCropTransform().transform(new RoundedCorners(cornerRadius));

            Glide.with(context)
                    .load(error)
                    .apply(options)
                    .into(into);
        }
    }
}
