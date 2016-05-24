package com.yiw.circledemo.widgets.videolist.visibility.calculator;

import android.graphics.Rect;
import android.view.View;

/**
 * @author Wayne
 */
public class VisibilityPercentsCalculator {

    /**
     * When this method is called, the implementation should provide a visibility percents in range 0 - 100 %
     * @param view the view which visibility percent should be calculated.
     *             Note: visibility doesn't have to depend on the visibility of a full view.
     *             It might be calculated by calculating the visibility of any inner View
     *
     * @return percents of visibility
     */
    public static int getVisibilityPercents(View view) {
        final Rect currentViewRect = new Rect();

        int percents = 100;

        int height = (view == null || view.getVisibility() != View.VISIBLE) ? 0 : view.getHeight();

        if (height == 0) {
            return 0;
        }

        view.getLocalVisibleRect(currentViewRect);

        if(viewIsPartiallyHiddenTop(currentViewRect)){
            // view is partially hidden behind the top edge
            percents = (height - currentViewRect.top) * 100 / height;
        } else if(viewIsPartiallyHiddenBottom(currentViewRect, height)){
            percents = currentViewRect.bottom * 100 / height;
        }

        return percents;
    }

    private static boolean viewIsPartiallyHiddenBottom(Rect currentViewRect, int height) {
        return currentViewRect.bottom > 0 && currentViewRect.bottom < height;
    }

    private static boolean viewIsPartiallyHiddenTop(Rect currentViewRect) {
        return currentViewRect.top > 0;
    }
}
