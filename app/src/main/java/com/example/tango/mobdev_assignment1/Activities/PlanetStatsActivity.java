package com.example.tango.mobdev_assignment1.Activities;

import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.tango.mobdev_assignment1.Game.PlanetsEnum;
import com.example.tango.mobdev_assignment1.R;

public class PlanetStatsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planet_stats);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setVisibility(View.INVISIBLE);
//        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
//        appBar.setVisibility(View.INVISIBLE);
        // Create the adapter that will return a fragment for each of the 8
        // sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(8);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_planet_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A planetary fragment containing a simple view.
     */
    public static class PlanetaryStatisticFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlanetaryStatisticFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlanetaryStatisticFragment newInstance(int sectionNumber) {
            PlanetaryStatisticFragment fragment = new PlanetaryStatisticFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_planet_stats, container, false);
            Bundle args = getArguments();
            PlanetsEnum planet = PlanetsEnum.getPlanet(args.getInt(ARG_SECTION_NUMBER) + 1);

            TextView title = (TextView) rootView.findViewById(R.id.planetTitle);
            ImageView image = (ImageView) rootView.findViewById(R.id.planetImageView);
            TextView diameter = (TextView) rootView.findViewById(R.id.PlanetSizeText);
            TextView mass = (TextView) rootView.findViewById(R.id.PlanetMassText);
            TextView gravity = (TextView) rootView.findViewById(R.id.PlanetGravityText);
            TextView distanceFromEarth = (TextView) rootView.findViewById(R.id.OrbitText);
            TextView distanceFromSun = (TextView) rootView.findViewById(R.id.DistanceFromSunText);

            String titleStr = "#" + planet.getOrder() + " " + planet.toString();
            String diameterStr = planet.getDiameter() + " km";
            String massStr = planet.getMass() + " x 10" + '\u00B2' + '\u2074';
            //Html.fromHtml(getString(R.string.Mass10ToThePower24)
            String gravityStr = String.valueOf(planet.getGravity()) + " G";
            String orbitStr = planet.getOrbit();
            String fromSunStr = planet.getDistanceFromSun() + " km";

            title.setText(titleStr);
            image.setImageResource(planet.getLargeImageResource());
            diameter.setText(diameterStr);
            mass.setText(massStr);
            gravity.setText(gravityStr);
            distanceFromEarth.setText(orbitStr);
            distanceFromSun.setText(fromSunStr);

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PlanetaryStatisticFragment.newInstance(0);
                case 1:
                    return PlanetaryStatisticFragment.newInstance(1);
                case 2:
                    return PlanetaryStatisticFragment.newInstance(2);
                case 3:
                    return PlanetaryStatisticFragment.newInstance(3);
                case 4:
                    return PlanetaryStatisticFragment.newInstance(4);
                case 5:
                    return PlanetaryStatisticFragment.newInstance(5);
                case 6:
                    return PlanetaryStatisticFragment.newInstance(6);
                case 7:
                    return PlanetaryStatisticFragment.newInstance(7);
            }

            // getItem is called to instantiate the fragment for the given page.
            // Return a PlanetaryStatisticFragment (defined as a static inner class below).
            return PlanetaryStatisticFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 8 total pages.
            return 8;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Mercury";
                case 1:
                    return "Venus";
                case 2:
                    return "Earth";
                case 3:
                    return "Mars";
                case 4:
                    return "Jupiter";
                case 5:
                    return "Saturn";
                case 6:
                    return "Uranus";
                case 7:
                    return "Neptune";
            }
            return null;
        }
    }
}
