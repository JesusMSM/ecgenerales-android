package es.elconfidencial.eleccionesgenerales2015.adapters;

/**
 * Created by MOONFISH on 14/07/2015.
 */

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.activities.MainActivity;
import es.elconfidencial.eleccionesgenerales2015.fragments.HomeTab;
import es.elconfidencial.eleccionesgenerales2015.fragments.NoticiasTab;
import es.elconfidencial.eleccionesgenerales2015.fragments.GraficosTab;
import es.elconfidencial.eleccionesgenerales2015.fragments.PresinderTab;


public class ViewPagerAdapter extends FragmentStatePagerAdapter{

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    private int[] imageResId = {
            R.drawable.tab_home_icon,
            R.drawable.tab_home_icon,
            R.drawable.tab_home_icon,
            R.drawable.tab_home_icon
    };


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            HomeTab homeTab = new HomeTab();
            return homeTab;
        }
        if(position == 1)
        {
            NoticiasTab noticiasTab = new NoticiasTab();
            return noticiasTab;
        }
        if(position == 2){
            GraficosTab graficosTab = new GraficosTab();
            return graficosTab;

        }
        else{
            PresinderTab presinderTab = new PresinderTab();
            return presinderTab;
        }


    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = MainActivity.context.getResources().getDrawable(imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString("  " + "\n" + " HOLA");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
        //return Titles[position];
    }

    public int getDrawableId(int position){
        //Here is only example for getting tab drawables
        return imageResId[position];
    }



    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}