package com.devtechnician.paledj;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;


public class BaseActivity extends SlidingFragmentActivity  {
	private int mTitleRes;
	protected SlidingMenu rightFrame;
    private ProgressDialog mDialog;
    private SherlockListFragment baseFrag;

    public BaseActivity(int titleRes) {
		this.mTitleRes = titleRes;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

        mDialog = new ProgressDialog(this);
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);

		setTitle(mTitleRes);
        setBehindContentView(R.layout.menu_frame);


		SlidingMenu menuRight = getSlidingMenu();
		menuRight.setMode(SlidingMenu.LEFT);
		menuRight.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		menuRight.setShadowWidth(5);
		menuRight.setFadeDegree(0.0f);
		menuRight.setBehindOffset(50);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(R.drawable.abs__ic_go);
		this.rightFrame = menuRight;

        FragmentManager fm = getSupportFragmentManager();

        baseFrag = (Media_FTP_Fragment)fm.findFragmentByTag("baseFragment");
        if (baseFrag == null) {
            baseFrag = new Media_FTP_Fragment();
            fm.beginTransaction().add(R.id.menu_frame,baseFrag,"baseFragment").commit();
        }
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			toggle();
			return true;
            /*case R.id.menu_people:
                BusProvider.getInstance().post(new ListMenuSelection(R.id.menu_people));
                return true;
            case R.id.menu_jobs:
                BusProvider.getInstance().post(new ListMenuSelection(R.id.menu_jobs));
                return true;
            case R.id.menu_news:
                BusProvider.getInstance().post(new ListMenuSelection(R.id.menu_news));
                return  true;
            case R.id.menu_events:
               BusProvider.getInstance().post(new ListMenuSelection(R.id.menu_events));

                return true;*/
            default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(this);

        super.onPause();
    }

    @Override
    public void onResume() {

        BusProvider.getInstance().register(this);
        super.onResume();
    }

}
