package com.group09.hti_thermostat;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayFragment extends Fragment implements ListView.OnItemClickListener, View.OnClickListener {
    private static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;



    private int DAY_ID;
    private String DAY_NAME;

    private String[] day_switches; // not day is in the opposite of night, but as in this day (i.e. monday)
    private String[] day_switch_types;
    private boolean[] day_switch_enabled;

    private OnFragmentInteractionListener mListener;

    public ListView lvDay;
    public FloatingActionButton fabDay;

    private ArrayAdapter<String> listAdapter;
    ArrayList<String> lItems;

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
        this.DAY_ID = mPage - 1;
        this.DAY_NAME = ThermostatData.days[DAY_ID];

        // Make local copies of this so that the GET requests do not overwrite impending changes.

        day_switches = Arrays.copyOf(WeekProgramActivity.cp_switches[DAY_ID], 10);
        day_switch_enabled = Arrays.copyOf(WeekProgramActivity.cp_switch_states[DAY_ID], 10);
        day_switch_types = Arrays.copyOf(WeekProgramActivity.cp_switch_types[DAY_ID], 10);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_day, container, false);
        lvDay = (ListView) view.findViewById(R.id.lvDay);
        lvDay.setOnItemClickListener(this);

        fabDay = (FloatingActionButton) view.findViewById(R.id.fabDay);
        fabDay.setOnClickListener(this);


        lItems = new ArrayList<String>();
        for(int i = 0; i < day_switches.length; i++){
            if(day_switch_enabled[i]){
                lItems.add("Switch to " + day_switch_types[i] + " temperature\nat " + day_switches[i]); // add it
            }
        }
        listAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.sample_day_row_view, R.id.tvSwitchTitle);
        listAdapter.addAll(lItems);
        lvDay.setAdapter(listAdapter);
        return view;
    }

    public void onButtonPressed(Uri uri) {}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int pos = position;
        final View fView = view;
        String text = String.valueOf(lvDay.getItemAtPosition(position));
        final String type = getTypeFromText(text);
        String time = text.substring(text.length() - 5, text.length());

        final Dialog d = new Dialog(this.getActivity());
        d.setTitle("Edit Switch");
        d.setContentView(R.layout.switch_edit);
        final Switch swDayNight = (Switch) d.findViewById(R.id.swDayNight);
        if(type.equals("day")){
            swDayNight.setChecked(false);
        }else{
            swDayNight.setChecked(true);
        }
        final TimePicker picker = (TimePicker) d.findViewById(R.id.timePicker);
        Button btnSave = (Button) d.findViewById(R.id.btnSave);
        Button btnDelete = (Button) d.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lItems.remove(pos); // remove from raw set
                listAdapter.clear();
                listAdapter.addAll(lItems);
                listAdapter.notifyDataSetChanged();
                d.dismiss();
                updateAndSaveSchedule();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTime = String.valueOf(picker.getHour()) + ":" + String.valueOf(picker.getMinute());
                String swType;
                if(!fiveOfEither().equals(type) && fiveOfEither() != "neither"){
                    swType = type;
                    if((swDayNight.isChecked() ? "night" : "day")!= type){
                        Snackbar.make(fView, "Failed to save: Only 5 switches of each type allowed!", Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                        d.dismiss();
                        return;
                    }
                }else{
                    swType = (swDayNight.isChecked()) ? "night" : "day";
                }
                String item = "Switch to " + swType + " temperature\nat " + GeneralHelper.correctTime(strTime);

                lItems.set(pos, item);
                listAdapter.clear();
                listAdapter.addAll(lItems);
                listAdapter.notifyDataSetChanged();
                updateAndSaveSchedule();
                d.dismiss();
            }
        });
        picker.setIs24HourView(true);
        picker.setHour(Integer.parseInt(time.split(":")[0])); // parse left of :
        picker.setMinute(Integer.parseInt(time.split(":")[1])); // parse right of :

        d.show();
    }

    public static String getTypeFromText(String text){
        if(text.toLowerCase().contains("day")){
            return "day";
        }else if(text.toLowerCase().contains("night")){
            return "night";
        }else{
            return "Unknown switch";
        }
    }

    private void updateAndSaveSchedule(){
        //TODO: this should update the arrays and call an api save.
        Arrays.fill(day_switch_enabled, false); // disable them all and only enable the ones we use.
        Arrays.fill(day_switches, "00:00");
        Arrays.fill(day_switch_types, "night");
        Arrays.fill(day_switch_types, 0, 4, "day");
        for(int i = 0; i < lItems.size(); i++){
            day_switches[i] = getTimeFromItem(lItems.get(i));
            day_switch_types[i] = getTypeFromText(lItems.get(i));
            day_switch_enabled[i] = true;
        }

        //correct day/night count.
        if(!isBalanced(day_switch_types)){
            String lesserType = getLesserType(day_switch_types);
            for(int i = 0; i < 10; i++){
                if(isBalanced(day_switch_types)) break;

                if(!day_switch_types[i].equals(lesserType) && day_switch_enabled[i] == false){
                    // switch it
                    day_switch_types[i] = swap_type(day_switch_types[i]);
                }
            }
        }

        WeekProgramActivity.cp_switches[DAY_ID] = Arrays.copyOf(this.day_switches, 10);
        WeekProgramActivity.cp_switch_types[DAY_ID] = Arrays.copyOf(this.day_switch_types, 10);
        WeekProgramActivity.cp_switch_states[DAY_ID] = Arrays.copyOf(this.day_switch_enabled, 10);

        ThermostatData.putWeekProgram(WeekProgramActivity.cp_switches, WeekProgramActivity.cp_switch_types, WeekProgramActivity.cp_switch_states);

        // Reload the fragment.
        reloadLV();
    }

    private void reloadLV(){
        // TODO
    }

    private String getLesserType(String[] types){
        int nCount = 0, dCount = 0;
        for(int i = 0; i < 10; i++){
            if(types[i].contains("day")){
                dCount++;
            }else{
                nCount++;
            }
        }
        return (nCount < dCount) ? "night" : "day";
    }

    private boolean isBalanced(String[] types){
        int nCount = 0, dCount = 0;
        for(int i = 0; i < 10; i++){
            if(types[i].contains("day")){
                dCount++;
            }else{
                nCount++;
            }
        }

        return (nCount == 5 && dCount == 5);
    }

    private String fiveOfEither(){
        int nightCount = 0, dayCount = 0;

        for(int i = 0; i < lItems.size(); i++){
            if(lItems.get(i).toLowerCase().contains("day")){
                dayCount++;
            }else{
                nightCount++;
            }
        }

        if(nightCount == 5) return "night";
        if(dayCount == 5) return "day";
        return "neither";
    }

    private static String swap_type(String type){
        if(type.equals("day")){
            return "night";
        }else{
            return "day";
        }
    }

    private static String getTimeFromItem(String item){
        return item.substring(item.length() - 5, item.length()).trim();
    }

    @Override
    public void onClick(View v) {
        // ONLY for floatingactionbutton. Do not use for anything else unless you implement a switch.
        if(lItems.size() > 9){
            Snackbar.make(v, "You have reached the maximum number of switches!", Snackbar.LENGTH_LONG)
                    .setAction("", null).show();
        }else{
            newSwitch(v);
        }
    }

    private void newSwitch(View view){
        final View fView = view;
        final Dialog d = new Dialog(this.getActivity());
        d.setTitle("New Switch");
        d.setContentView(R.layout.switch_edit);
        final Switch swDayNight = (Switch) d.findViewById(R.id.swDayNight);

        final TimePicker picker = (TimePicker) d.findViewById(R.id.timePicker);
        Button btnSave = (Button) d.findViewById(R.id.btnSave);
        Button btnDelete = (Button) d.findViewById(R.id.btnDelete);
        btnDelete.setWidth(0); // make it essentially invisible.

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTime = String.valueOf(picker.getHour()) + ":" + String.valueOf(picker.getMinute());
                String swType = (swDayNight.isChecked()) ? "night" : "day";
                if(fiveOfEither().equals(swType)){
                        Snackbar.make(fView, "Failed to save: Only 5 switches of each type allowed!", Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                        d.dismiss();
                        return;
                }
                String item = "Switch to " + swType + " temperature\nat " + GeneralHelper.correctTime(strTime);

                lItems.add(item);
                listAdapter.clear();
                listAdapter.addAll(lItems);
                listAdapter.notifyDataSetChanged();
                updateAndSaveSchedule();
                d.dismiss();
            }
        });
        picker.setIs24HourView(true);
        picker.setHour(10);
        picker.setMinute(10);

        d.show();
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
