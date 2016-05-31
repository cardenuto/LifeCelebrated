package info.anth.lifecelebrated.AddLocationSteps;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.anth.lifecelebrated.Helpers.Helper;
import info.anth.lifecelebrated.R;

/**
 * Created by Primary on 4/6/2016.
 *
 * Blank step number
 */
public class BlankFragment extends Fragment {

    /**
     * The fragment argument representing the page number for this
     * fragment.
     */
    private static final String ARG_PAGE_NUMBER = "page_number";

    public BlankFragment() { }

    /**
     * Returns a new instance of this fragment for the given page
     * number.
     */
    public static BlankFragment newInstance(int pageNumber) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get argument data
        int position = getArguments().getInt(ARG_PAGE_NUMBER);

        //View rootView = inflater.inflate(R.layout.fragment_steps, container, false);
        final View rootView = inflater.inflate(R.layout.fragment_al_blank, container, false);

        // set page parameters
        int currentPage = position + 1;
        final Resources rw = getResources();
        final int stepArrayNo = rw.getIntArray(R.array.step_array_no)[position];

        // -------
        // Process the heading
        //
        TextView headingStepTextView = (TextView) rootView.findViewById(R.id.heading_step);
        TextView headingStepNoTextView = (TextView) rootView.findViewById(R.id.heading_number);
        TextView headingStepLineTextView = (TextView) rootView.findViewById(R.id.heading_line);
        TextView headingStepHRLine1TextView = (TextView) rootView.findViewById(R.id.heading_hr_line1);
        TextView headingStepHRLine2TextView = (TextView) rootView.findViewById(R.id.heading_hr_line2);
        TextView headingTitleTextView = (TextView) rootView.findViewById(R.id.heading_title);
        TextView headingTextView = (TextView) rootView.findViewById(R.id.heading_text);

        int headingColor;

        // set the colors for the number
        try {
            headingColor = rw.getIntArray(R.array.step_text_color)[stepArrayNo];
            headingStepTextView.setTextColor(headingColor);
            headingStepNoTextView.setTextColor(headingColor);
            headingStepLineTextView.setBackgroundColor(headingColor);
            headingStepHRLine1TextView.setBackgroundColor(headingColor);
            headingStepHRLine2TextView.setBackgroundColor(headingColor);
        } catch (Exception e) {
            // do nothing at this time
        }

        // set the text values
        headingStepNoTextView.setText(String.valueOf(currentPage));
        headingTitleTextView.setText(Helper.resourceString(getContext(), R.array.step_title, stepArrayNo));
        headingTextView.setText(Html.fromHtml(Helper.resourceString(getContext(), R.array.step_text, stepArrayNo)));
        //
        // -------

        return rootView;
    }
}
