package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;

/**
 * Purple gradient card that shows the local communication streak on a user profile.
 */
@SuppressLint("ViewConstructor")
public class StreakCardView extends FrameLayout {

    private static final int[] MILESTONES = {3, 7, 27, 100, 365};

    private final TextView daysView;
    private final TextView totalView;
    private final TextView[] milestoneViews = new TextView[MILESTONES.length];

    private int currentStreak;
    private int totalDays;

    public StreakCardView(Context context) {
        super(context);
        setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(6), AndroidUtilities.dp(10), AndroidUtilities.dp(6));

        final LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(14), AndroidUtilities.dp(16), AndroidUtilities.dp(14));
        final GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{0xFF1D0B33, 0xFF2E1065, 0xFF5B21B6}
        );
        background.setCornerRadius(AndroidUtilities.dp(18));
        card.setBackground(background);
        addView(card, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        final LinearLayout titleRow = new LinearLayout(context);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);

        final TextView title = new TextView(context);
        title.setText(R.string.ProfileStreak);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        title.setTypeface(AndroidUtilities.bold());
        title.setTextColor(0xFFE9D5FF);
        titleRow.addView(title, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1f));

        final TextView mutual = new TextView(context);
        mutual.setText(R.string.ProfileStreakMutual);
        mutual.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        mutual.setTextColor(0xFFC4B5FD);
        titleRow.addView(mutual, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        card.addView(titleRow, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        final LinearLayout daysRow = new LinearLayout(context);
        daysRow.setOrientation(LinearLayout.HORIZONTAL);
        daysRow.setGravity(Gravity.CENTER_VERTICAL);
        daysRow.setPadding(0, AndroidUtilities.dp(4), 0, 0);

        final TextView flame = new TextView(context);
        flame.setText("\uD83D\uDD25");
        flame.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        daysRow.addView(flame, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        daysView = new TextView(context);
        daysView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        daysView.setTypeface(AndroidUtilities.bold());
        daysView.setTextColor(Color.WHITE);
        daysView.setPadding(AndroidUtilities.dp(6), 0, 0, 0);
        daysRow.addView(daysView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        card.addView(daysRow, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        totalView = new TextView(context);
        totalView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        totalView.setTextColor(0xFFDDD6FE);
        totalView.setPadding(0, AndroidUtilities.dp(2), 0, AndroidUtilities.dp(10));
        card.addView(totalView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        final LinearLayout milestonesRow = new LinearLayout(context);
        milestonesRow.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < MILESTONES.length; i++) {
            final TextView milestone = new TextView(context);
            milestone.setGravity(Gravity.CENTER);
            milestone.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            milestone.setTypeface(Typeface.DEFAULT_BOLD);
            milestone.setPadding(AndroidUtilities.dp(9), AndroidUtilities.dp(4), AndroidUtilities.dp(9), AndroidUtilities.dp(4));
            milestoneViews[i] = milestone;
            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
            if (i < MILESTONES.length - 1) {
                lp.rightMargin = AndroidUtilities.dp(6);
            }
            milestonesRow.addView(milestone, lp);
        }
        card.addView(milestonesRow, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
    }

    public void setStreakData(int streak, int totalDays) {
        this.currentStreak = streak;
        this.totalDays = totalDays;

        daysView.setText(getResources().getQuantityString(R.plurals.ProfileStreakDays, streak, streak));
        totalView.setText(getResources().getQuantityString(R.plurals.ProfileStreakTotal, totalDays, totalDays));

        for (int i = 0; i < MILESTONES.length; i++) {
            final int milestone = MILESTONES[i];
            final TextView view = milestoneViews[i];
            view.setText(String.valueOf(milestone));
            final boolean reached = totalDays >= milestone;
            final GradientDrawable chip = new GradientDrawable();
            chip.setCornerRadius(AndroidUtilities.dp(9));
            chip.setColor(reached ? 0xFF8B5CF6 : 0x33FFFFFF);
            view.setBackground(chip);
            view.setTextColor(reached ? Color.WHITE : 0xFFC4B5FD);
        }
    }

    public int getCurrentStreak() {
        return currentStreak;
    }
}
