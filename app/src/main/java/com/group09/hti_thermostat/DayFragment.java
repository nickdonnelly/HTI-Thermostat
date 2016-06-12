package com.group09.hti_thermostat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayFragment extends Fragment {
    private static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    private int DAY_ID;
    private int DAY_NAME;

    private OnFragmentInteractionListener mListener;

    public static TextView tvTest;

    public DayFragment() {
        // Required empty public constructor
    }

    public static DayFragment newInstance(int index) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putInt("ARG_PAGE", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        tvTest = (TextView) this.findViewById(R.id.textViewTest);
        View view = inflater.inflate(R.layout.fragment_day, container, false);
        tvTest = (TextView) view;
        tvTest.setText("This is page" + String.valueOf(this.mPage));
        return view;
    }

    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
