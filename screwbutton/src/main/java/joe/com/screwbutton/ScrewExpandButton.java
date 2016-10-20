package joe.com.screwbutton;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * Description
 * Created by chenqiao on 2016/10/19.
 */
public class ScrewExpandButton extends CircleButton implements View.OnClickListener {
    private int subBtnSize;
    private ViewGroup rootLayout;
    private ArrayList<CircleButton> buttons;
    private boolean isExpand = false;
    private FrameLayout container;
    private ArrayList<ScrewAnimator> animators;
    private OnBtnClickListener btnClickListener;

    public ScrewExpandButton(Context context) {
        this(context, null);
    }

    public ScrewExpandButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrewExpandButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        buttons = new ArrayList<>();
        animators = new ArrayList<>();
        if (context instanceof Activity) {
            rootLayout = (ViewGroup) ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        }
        container = new FrameLayout(context);
        setOnClickListener(this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrewExpandButton, defStyleAttr, 0);
        subBtnSize = a.getDimensionPixelSize(R.styleable.ScrewExpandButton_subbutton_size, -1);
        a.recycle();
    }

    public void setOnBtnClickListener(OnBtnClickListener btnClickListener) {
        this.btnClickListener = btnClickListener;
    }

    public void addButton(Drawable drawable, int btnId) {
        CircleButton btn = new CircleButton(getContext());
        btn.setImageDrawable(drawable);
        btn.setAlpha(0f);
        btn.setId(btnId);
        buttons.add(btn);
        btn.setOnClickListener(buttonsClickListener);
    }

    public void removeButton(int btnId) {
        for (CircleButton button : buttons) {
            if (button.getId() == btnId) {
                buttons.remove(button);
                button.setOnClickListener(null);
                break;
            }
        }
    }

    private OnClickListener buttonsClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (btnClickListener != null) {
                btnClickListener.onBtnClick(v.getId());
            }
        }
    };

    @Override
    public void onClick(View v) {
        anchor = getAbsoluteXAndY();
        if (!isExpand) {
            animators.clear();
            expand();
            isExpand = true;
        } else {
            for (ScrewAnimator animator : animators) {
                animator.cancelAnimator();
            }
            animators.clear();
            collapse();
            isExpand = false;
        }
    }

    public void collapse() {
        if (rootLayout != null) {
            rootLayout.removeView(container);
        }
        container.removeAllViews();
    }

    int[] anchor;

    public void expand() {
        if (subBtnSize < 0) {
            subBtnSize = getWidth();
        }
        if (rootLayout != null) {
            rootLayout.addView(container, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        for (int i = 0; i < buttons.size(); i++) {
            CircleButton btn = buttons.get(i);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(subBtnSize, subBtnSize);
            params.leftMargin = anchor[0];
            params.topMargin = anchor[1];
            container.addView(btn, params);
            ScrewAnimator animator = new ScrewAnimator(btn, i, params);
            animator.start();
            animators.add(animator);
        }
    }

    private int[] getAbsoluteXAndY() {
        int[] result = new int[2];
        result[0] = (int) getX();
        result[1] = (int) getY();
        ViewParent parent = getParent();
        while (parent != null) {
            if (parent instanceof View) {
                if (((View) parent).getId() != android.R.id.content) {
                    result[0] += ((View) parent).getX();
                    result[1] += ((View) parent).getY();
                } else {
                    return result;
                }
            }
            parent = parent.getParent();
        }
        return result;
    }


    public interface OnBtnClickListener {
        void onBtnClick(int btnId);
    }

    public class ScrewAnimator {
        private int index;
        private FrameLayout.LayoutParams params;
        private CircleButton target;
        private AnimatorSet set;

        ScrewAnimator(CircleButton target, int index, FrameLayout.LayoutParams params) {
            this.target = target;
            this.index = index;
            this.params = params;
            init();
        }

        private void init() {
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(target, "alpha", 0f, 1f);
            alphaAnimator.setDuration(300 + 150 * (index + 1));
            ValueAnimator animator = ValueAnimator.ofFloat(0f, index / 7f);
            animator.setTarget(target);
            animator.setDuration(300 + 150 * (index + 1));
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float t = (float) animation.getAnimatedValue();
                    float r = (getWidth() + 5) * (1 + t);
                    int x = (int) (r * Math.cos(Math.toRadians(t * 360)));
                    int y = (int) (r * Math.sin(Math.toRadians(t * 360)));
                    params.leftMargin = anchor[0] + x;
                    params.topMargin = anchor[1] + y;
                    container.updateViewLayout(target, params);
                }
            });
            set = new AnimatorSet();
            set.play(alphaAnimator).with(animator);
            set.start();
        }

        void start() {
            if (set != null) {
                set.start();
            }
        }

        void addListener(Animator.AnimatorListener listener) {
            if (set != null) {
                set.addListener(listener);
            }
        }

        void cancelAnimator() {
            if (set != null) {
                set.cancel();
            }
        }
    }
}