package EspressoTests;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class CustomMatchers {

    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }

    public static Matcher<View> withDrawable(final int expectedId) {
        return new TypeSafeMatcher<View>() {
            String resourceName;

            private Bitmap getBitmap(Drawable drawable) {
                Bitmap result;
                if (drawable instanceof BitmapDrawable) {
                    result = ((BitmapDrawable) drawable).getBitmap();
                } else {
                    int width = drawable.getIntrinsicWidth();
                    int height = drawable.getIntrinsicHeight();
                    // Some drawables have no intrinsic width - e.g. solid colours.
                    if (width <= 0) {
                        width = 1;
                    }
                    if (height <= 0) {
                        height = 1;
                    }

                    result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(result);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);
                }
                return result;
            }

            @Override
            protected boolean matchesSafely(View target) {
                if (!(target instanceof ImageView)){
                    return false;
                }
                ImageView imageView = (ImageView) target;
                if (expectedId < 0){
                    return imageView.getDrawable() == null;
                }
                Resources resources = target.getContext().getResources();
                Drawable expectedDrawable = resources.getDrawable(expectedId);
                resourceName = resources.getResourceEntryName(expectedId);

                if (expectedDrawable == null) {
                    return false;
                }
                return getBitmap(expectedDrawable).sameAs(getBitmap(imageView.getDrawable()));
            }


            @Override
            public void describeTo(Description description) {
                description.appendText("with drawable from resource id: ");
                description.appendValue(expectedId);
                if (resourceName != null) {
                    description.appendText("[");
                    description.appendText(resourceName);
                    description.appendText("]");
                }
            }
        };
    }
}
