package ai.tomorrow.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;


/**
 * Demonstration of using fragments to implement different activity layouts.
 * This sample provides a different layout (and activity flow) when run in
 * landscape.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_MAIN = "MainActivity";

    //BEGIN_INCLUDE(main)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG_MAIN, "onCreate.");

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(TAG_MAIN, "onStart.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(TAG_MAIN, "onResume.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(TAG_MAIN, "onPause.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(TAG_MAIN, "onStop.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG_MAIN, "onDestroy.");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.w(TAG_MAIN, "onDetachedFromWindow.");
    }

//END_INCLUDE(main)

    /**
     * This is a secondary activity, to show what the user has selected
     * when the screen is not large enough to show it all in one activity.
     */
//BEGIN_INCLUDE(details_activity)
    public static class DetailsActivity extends FragmentActivity {

        private static final String TAG = "DetailsActivity";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.w(TAG, "onCreate.");

            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                // If the screen is now in landscape mode, we can show the
                // dialog in-line with the list so we don't need this activity.
                finish();
                return;
            }

            if (savedInstanceState == null) {
                // During initial setup, plug in the details fragment.
                DetailsFragment details = new DetailsFragment();
                details.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
            }
        }

        @Override
        protected void onStart() {
            super.onStart();
            Log.w(TAG, "onStart.");
        }

        @Override
        protected void onResume() {
            super.onResume();
            Log.w(TAG, "onResume.");
        }

        @Override
        protected void onPause() {
            super.onPause();
            Log.w(TAG, "onPause.");
        }

        @Override
        protected void onStop() {
            super.onStop();
            Log.w(TAG, "onStop.");
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            Log.w(TAG, "onDestroy.");
        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            Log.w(TAG, "onDetachedFromWindow.");
        }
    }
//END_INCLUDE(details_activity)

    /**
     * This is the "top-level" fragment, showing a list of items that the
     * user can pick.  Upon picking an item, it takes care of displaying the
     * data to the user as appropriate based on the currrent UI layout.
     */
//BEGIN_INCLUDE(titles)
    public static class TitlesFragment extends ListFragment {
        boolean dualPane;
        int curCheckPosition = 0;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Populate list with our static array of titles.
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_activated_1, Shakespeare.TITLES));

            // Check to see if we have a frame in which to embed the details
            // fragment directly in the containing UI.
            View detailsFrame = getActivity().findViewById(R.id.details);
            dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

            if (savedInstanceState != null) {
                // Restore last state for checked position.
                curCheckPosition = savedInstanceState.getInt("curChoice", 0);
            }

            if (dualPane) {
                // In dual-pane mode, the list view highlights the selected item.
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                // Make sure our UI is in the correct state.
                showDetails(curCheckPosition);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("curChoice", curCheckPosition);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            showDetails(position);
        }

        /**
         * Helper function to show the details of a selected item, either by
         * displaying a fragment in-place in the current UI, or starting a
         * whole new activity in which it is displayed.
         */
        void showDetails(int index) {
            curCheckPosition = index;

            if (dualPane) {
                // We can display everything in-place with fragments, so update
                // the list to highlight the selected item and show the data.
                getListView().setItemChecked(index, true);

                // Check what fragment is currently shown, replace if needed.
                DetailsFragment details = (DetailsFragment)
                        getFragmentManager().findFragmentById(R.id.details);
                if (details == null || details.getShownIndex() != index) {
                    // Make new fragment to show this selection.
                    details = DetailsFragment.newInstance(index);

                    // Execute a transaction, replacing any existing fragment
                    // with this one inside the frame.
                    FragmentTransaction ft = getFragmentManager().beginTransaction();

                    ft.replace(R.id.details, details);

                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }

            } else {
                // Otherwise we need to launch a new activity to display
                // the dialog fragment with selected text.
                Intent intent = new Intent();
                intent.setClass(getActivity(), DetailsActivity.class);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        }
    }
//END_INCLUDE(titles)

    /**
     * This is the secondary fragment, displaying the details of a particular
     * item.
     */
//BEGIN_INCLUDE(details)
    public static class DetailsFragment extends Fragment {

        private static final String TAG = "DetailsFragment";

        /**
         * Create a new instance of DetailsFragment, initialized to
         * show the text at 'index'.
         */
        public static DetailsFragment newInstance(int index) {
            DetailsFragment f = new DetailsFragment();

            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putInt("index", index);
            f.setArguments(args);

            return f;
        }

        public int getShownIndex() {
            return getArguments().getInt("index", 0);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.w(TAG, "onCreateView.");
            if (container == null) {
                // We have different layouts, and in one of them this
                // fragment's containing frame doesn't exist. The fragment
                // may still be created from its saved state, but there is
                // no reason to try to create its view hierarchy because it
                // isn't displayed. Note this isn't needed -- we could just
                // run the code below, where we would create and return the
                // view hierarchy; it would just never be used.
                return null;
            }

            ScrollView scroller = new ScrollView(getActivity());
            TextView text = new TextView(getActivity());
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    4, getActivity().getResources().getDisplayMetrics());
            text.setPadding(padding, padding, padding, padding);
            scroller.addView(text);
            text.setText(Shakespeare.DIALOGUE[getShownIndex()]);
            return scroller;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.w(TAG, "onCreate.");
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Log.w(TAG, "onActivityCreated.");
        }

        @Override
        public void onStart() {
            super.onStart();
            Log.w(TAG, "onStart.");
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.w(TAG, "onResume.");
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.w(TAG, "onPause.");
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.w(TAG, "onStop.");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.w(TAG, "onDestroy.");
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            Log.w(TAG, "onAttach.");
        }

        @Override
        public void onDetach() {
            super.onDetach();
            Log.w(TAG, "onDetach.");
        }
    }
//END_INCLUDE(details)
}

