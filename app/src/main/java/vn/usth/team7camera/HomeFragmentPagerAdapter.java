package vn.usth.team7camera;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 4;

    private String[] titles;

    public HomeFragmentPagerAdapter(FragmentManager fm, String [] titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
// number of pages for a ViewPager
    public Fragment getItem(int page) {
        switch (page) {
            case 0:
                return new CamerasFragment();
            case 1:
                return new EventsFragment();
            case 2:
                return new SnapshotsFragment();
            case 3:
                return new SettingsFragment();
        }
        return new Fragment();
    }

    @Override
    public CharSequence getPageTitle(int page) {
// returns a tab title corresponding to the specified page return titles[page];
        return titles[page];
    }
}