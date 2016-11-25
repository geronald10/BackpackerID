package goronald.web.id.backpackerid;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import at.markushi.ui.CircleButton;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class HomeFragment extends Fragment {

    private LinearLayout mBudget,mButton;
    private EditText etBudget;
    private TextView mBack;
    private ValueAnimator mAnimator;
    private CircleButton mReady;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_budget, container, false);


        mBudget = (LinearLayout)rootView.findViewById(R.id.llBudget);
        mButton = (LinearLayout)rootView.findViewById(R.id.llButton);
        etBudget = (EditText)rootView.findViewById(R.id.etBudget);
        mBack = (TextView)rootView.findViewById(R.id.tvGoBack);
        mBack.setOnClickListener(operation);
        mReady = (CircleButton)rootView.findViewById(R.id.btnReady);
        mReady.setOnClickListener(operation);

        mButton.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mButton.getViewTreeObserver().removeOnPreDrawListener(this);
                mButton.setVisibility(View.GONE);
                final int witdthSpec = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
                final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                mButton.measure(witdthSpec,heightSpec);
                mAnimator = slideAnimator(0,mButton.getMeasuredHeight());


                return true;
            }
        });
        etBudget.setOnEditorActionListener(new EditText.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH ||
                        i == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == keyEvent.ACTION_DOWN && keyEvent.getKeyCode() == keyEvent.KEYCODE_ENTER){

                    expand();
                    InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(),0);
                    return true;
//                    if (!keyEvent.isShiftPressed()){
//                        return true;
//                    }
                }
                return false;
            }
        });
//        initializeScreen(rootView);

        return rootView;
    }

    View.OnClickListener operation = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.tvGoBack:
                    collapse();
                    break;
                case R.id.btnReady:
                    goingIntent(ItemListActivity.class);
                    break;
            }
        }
    };
    private void goingIntent(Class x){
        Intent intent = new Intent(getContext(),x);
        startActivity(intent);
//        finish();
    }
    private void expand(){
        mButton.setVisibility(View.VISIBLE);
        mBudget.setVisibility(View.GONE);

        mAnimator.start();
    }

    private void collapse(){
        int finalHeight = mButton.getHeight();
        ValueAnimator mAnimator = slideAnimator(finalHeight,0);
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mButton.setVisibility(View.GONE);
                mBudget.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end){
        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value =(Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mButton.getLayoutParams();
                layoutParams.height = value;
                mButton.setLayoutParams(layoutParams);
            }
        });

        return animator;
    }
}
