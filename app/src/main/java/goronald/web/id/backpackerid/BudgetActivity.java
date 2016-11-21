package goronald.web.id.backpackerid;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BudgetActivity extends AppCompatActivity {

    private LinearLayout mBudget,mButton;
    private EditText etBudget;

    private ValueAnimator mAnimator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        mBudget = (LinearLayout)findViewById(R.id.llBudget);
        mButton = (LinearLayout)findViewById(R.id.llButton);
        etBudget = (EditText)findViewById(R.id.etBudget);

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
                    return true;


//                    if (!keyEvent.isShiftPressed()){
//                        return true;
//                    }
                }
                return false;
            }
        });

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
