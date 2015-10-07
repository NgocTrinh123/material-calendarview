package com.prolificinteractive.materialcalendarview.sample;

import android.support.v7.app.AppCompatActivity;

/**
 * Shows off the most basic usage
 * TODO enable
 */
public class BasicActivityDecorated extends AppCompatActivity {

//    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
//
//    @Bind(R.id.calendarView)
//    MaterialCalendarView widget;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_basic);
//        ButterKnife.bind(this);
//
//        widget.setOnDateChangedListener(this);
//        widget.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
//
//        Calendar calendar = Calendar.getInstance();
//        widget.setSelectedDate(calendar.getTime());
//
//        calendar.set(calendar.get(Calendar.YEAR), Calendar.JANUARY, 1);
//        widget.setMinimumDate(calendar.getTime());
//
//        calendar.set(calendar.get(Calendar.YEAR), Calendar.DECEMBER, 31);
//        widget.setMaximumDate(calendar.getTime());
//
//        widget.addDecorators(
//                new MySelectorDecorator(this),
//                new HighlightWeekendsDecorator(),
//                oneDayDecorator
//        );
//
//        new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
//    }
//
//    @Override
//    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
//        //If you change a decorate, you need to invalidate decorators
//        oneDayDecorator.setDate(date.getDate());
//        widget.invalidateDecorators();
//    }
//
//    /**
//     * Simulate an API call to show how to add decorators
//     */
//    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {
//
//        @Override
//        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.MONTH, -2);
//            ArrayList<CalendarDay> dates = new ArrayList<>();
//            for (int i = 0; i < 30; i++) {
//                CalendarDay day = CalendarDay.from(calendar);
//                dates.add(day);
//                calendar.add(Calendar.DATE, 5);
//            }
//
//            return dates;
//        }
//
//        @Override
//        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
//            super.onPostExecute(calendarDays);
//
//            if (isFinishing()) {
//                return;
//            }
//
//            widget.addDecorator(new EventDecorator(Color.RED, calendarDays));
//        }
//    }
}
