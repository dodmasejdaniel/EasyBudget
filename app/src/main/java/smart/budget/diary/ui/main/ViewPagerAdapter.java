package smart.budget.diary.ui.main;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import smart.budget.diary.ui.main.history.HistoryFragment;
import smart.budget.diary.ui.main.home.HomeFragment;


//Adapter for managing the view pager (in our case, home and history tab) in the main activity.
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private static int TAB_COUNT = 2;
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return HomeFragment.newInstance();
            case 1:
                return HistoryFragment.newInstance();
        }
        return null;
    }
    @Override
    public int getCount() {
        return TAB_COUNT;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Home";
            case 1:
                return "History";
        }
        return super.getPageTitle(position);
    }
}