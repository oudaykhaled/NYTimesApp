package ouday.challenge.com.oudaychallenge.area.about_me.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ouday.challenge.com.oudaychallenge.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutMe extends Fragment {


    public AboutMe() {}

    public static Fragment newInstance() {
        return new AboutMe();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_me, container, false);
    }

}
