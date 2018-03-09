package tw.yalan.cafeoffice.activitys.loadtohome

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_animation_splash.*
import tw.yalan.cafeoffice.R
import tw.yalan.cafeoffice.adapter.SplashPagerAdapter
import tw.yalan.cafeoffice.fragments.splash.*
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import tw.yalan.cafeoffice.common.kt.bindString
import tw.yalan.cafeoffice.common.kt.onSafeClick


/**
 * Created by Yalan Ding on 2017/8/20.
 */
class AnimationSplashActivity : AppCompatActivity() {
    private val MIN_SCALE = 0.9f
    lateinit var adapter: SplashPagerAdapter
    var layer: Array<Int> = arrayOf(R.id.imageView, R.id.textView2, R.id.textView3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation_splash)
        val list = arrayListOf(SplashFirstFragment(),
                SplashSecondFragment(),
                SplashThirdFragment(),
                SplashFourFragment(),
                SplashFiveFragment())
        adapter = SplashPagerAdapter(supportFragmentManager
                , list)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
        viewPager.setPageTransformer(true,
                { page, position ->
                    var scrollXOffset = page.width * 1f

                    for (id in layer) {
                        val view = page.findViewById<View>(id)
                        view?.translationX = scrollXOffset * position
                        scrollXOffset *= 1.5f
                    }
                    if (position <= 1) {
                        val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                        page.scaleX = scaleFactor
                        page.scaleY = scaleFactor
                    }
                }
        )
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 4) {
                    tvSkip.text = R.string.button_close.bindString(this@AnimationSplashActivity)
                    YoYo.with(Techniques.FadeOut)
                            .duration(500)
                            .playOn(circleIndicator)
                }else if(circleIndicator.alpha == 0f){
                    tvSkip.text = R.string.button_skip.bindString(this@AnimationSplashActivity)
                    YoYo.with(Techniques.FadeIn)
                            .duration(500)
                            .playOn(circleIndicator)
                }
            }
        })
        circleIndicator.setViewPager(viewPager)
        list.forEach {
            viewPager.addOnPageChangeListener(it)
        }

        tvSkip.onSafeClick {
            finish()
        }

        ModelManager.get().getUserModel().putSplashWatched(true)
    }
}