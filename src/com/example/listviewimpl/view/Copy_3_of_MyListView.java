package com.example.listviewimpl.view;

import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.OverScroller;

public class Copy_3_of_MyListView extends AdapterView<Adapter> {

	static final int TOUCH_MODE_REST = -1;

	static final int TOUCH_MODE_DOWN = 0;

	static final int TOUCH_MODE_SCROLL = 1;

	static final int TOUCH_MODE_FLING = 2;

	private static final int INVALID_POINTER = -1;

	private final int LAYOUT_MODE_BELOW = -1;
	private final int LAYOUT_MODE_ABOVE = 0;

	private int mTouchSlop;
	private Adapter mAdapter;
	private float mVelocityScale = 1.0f;

	private int mLastItemPosition;
	private int mFirstItemPosition;
	private float mScrolledDistance;

	private Motion mMotion = new Motion();
	private Runnable mLongPressRunnable;
	private int mTouchMode;
	private VelocityTracker mVelocityTracker;

	private int mMinimumVelocity;

	private int mMaximumVelocity;

	private int mOverscrollDistance;
	private int mOverflingDistance;

	private int mActivePointerId;

	private FlingRunnable mFlingRunnable;

	private final LinkedList<View> mCachedItemViews = new LinkedList<View>();

	final class Motion {
		float mY;
		float mX;
		float mPointY;
	}

	public Copy_3_of_MyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Copy_3_of_MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Copy_3_of_MyListView(Context context) {
		super(context);
		init();
	}

	private void init() {
		ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
		mOverscrollDistance = configuration.getScaledOverscrollDistance();
		mOverflingDistance = configuration.getScaledOverflingDistance();
	}

	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		mAdapter = adapter;
	}

	@Override
	public View getSelectedView() {
		return null;
	}

	@Override
	public void setSelection(int position) {
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mAdapter == null)
			return;

		if (getChildCount() == 0) {
			initList(left);
			return;
		}

		if (mScrolledDistance == 0)
			return;

		final float tScrolledDistance = mScrolledDistance;
		if (tScrolledDistance > 0) {
			removeDownNonVisibleViews(tScrolledDistance);
			fillListUp(getChildAt(0).getTop(), tScrolledDistance, left);
			layoutPositionItemsUp(left);
		} else {
			removeUpNonVisibleViews(tScrolledDistance);
			fillListDown(getChildAt(getChildCount() - 1).getBottom(), tScrolledDistance, left);
			layoutPositionItemsDown(left);
		}

		invalidate();
	}

	private void removeUpNonVisibleViews(float offset) {
		View removeChild = getChildAt(0);
		while (removeChild != null && removeChild.getBottom() + offset < 0) {
			removeViewInLayout(removeChild);
			mCachedItemViews.addLast(removeChild);
			mFirstItemPosition++;
			removeChild = getChildAt(0);
		}
	}

	private void removeDownNonVisibleViews(float offset) {
		View removeChild = getChildAt(getChildCount() - 1);
		while (removeChild != null && removeChild.getTop() + offset > getHeight()) {
			removeViewInLayout(removeChild);
			mCachedItemViews.addLast(removeChild);
			mLastItemPosition--;
			removeChild = getChildAt(getChildCount() - 1);
		}
	}

	private void fillListUp(int topEdge, float offset, int parentLeft) {
		View cachedView;
		View newTopChild = null;
		while (topEdge + offset > 0 && mFirstItemPosition > 0) {
			mFirstItemPosition--;
			cachedView = getCachedView();
			newTopChild = mAdapter.getView(mFirstItemPosition, cachedView, this);
			addAndMeasureChild(newTopChild, LAYOUT_MODE_ABOVE, cachedView == newTopChild);
			topEdge -= newTopChild.getMeasuredHeight();
		}
		if (newTopChild != null) {
			int width = newTopChild.getMeasuredWidth();
			int height = newTopChild.getMeasuredHeight();
			newTopChild.layout(parentLeft, -height, parentLeft + width, 0);
		}
	}

	private void fillListDown(int bottomEdge, float offset, int parentLeft) {
		View cachedView;
		View newBottomChild = null;
		while (bottomEdge + offset < getHeight() && mLastItemPosition < mAdapter.getCount()) {
			cachedView = getCachedView();
			newBottomChild = mAdapter.getView(mLastItemPosition, cachedView, this);
			addAndMeasureChild(newBottomChild, LAYOUT_MODE_BELOW, cachedView == newBottomChild);
			mLastItemPosition++;
			bottomEdge += newBottomChild.getMeasuredHeight();
		}
		if (newBottomChild != null) {
			int width = newBottomChild.getMeasuredWidth();
			int height = newBottomChild.getMeasuredHeight();
			int ph = getHeight();
			newBottomChild.layout(parentLeft, ph, parentLeft + width, ph + height);
		}
	}

	private View getCachedView() {
		if (mCachedItemViews.size() != 0) {
			return mCachedItemViews.removeFirst();
		}
		return null;
	}

	private void initList(int parentLeft) {
		mLastItemPosition = 0;
		mFirstItemPosition = 0;
		for (int BottomEdge = 0; BottomEdge < getHeight() && mLastItemPosition < mAdapter.getCount(); mLastItemPosition++) {
			View newBottomChild = mAdapter.getView(mLastItemPosition, null, this);
			addAndMeasureChild(newBottomChild, LAYOUT_MODE_BELOW, false);
			int width = newBottomChild.getMeasuredWidth();
			int height = newBottomChild.getMeasuredHeight();
			newBottomChild.layout(parentLeft, BottomEdge, parentLeft + width, BottomEdge + height);
			BottomEdge += height;
		}
	}

	private void addAndMeasureChild(View child, int layoutMode, boolean b) {
		LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		addViewInLayout(child, layoutMode, params, true);
		if (!b)
			child.measure(MeasureSpec.EXACTLY | getWidth(), MeasureSpec.UNSPECIFIED);
	}

	private void layoutPositionItemsUp(int parentLeft) {
		int top = (int) (getChildAt(0).getTop() + mScrolledDistance);

//		if (mFirstItemPosition == 0 && top > 0)
//			top = 0;

		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();

			child.layout(parentLeft, top, parentLeft + width, top + height);
			top += height;
		}
	}

	private void layoutPositionItemsDown(int parentLeft) {
		int tCount = getChildCount() - 1;
		int bottom = (int) (getChildAt(tCount).getBottom() + mScrolledDistance);

//		if (mLastItemPosition == mAdapter.getCount() && bottom > getHeight())
//			bottom = getHeight();

		for (int i = tCount; i >= 0; i--) {
			View child = getChildAt(i);
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();

			child.layout(parentLeft, bottom - height, parentLeft + width, bottom);
			bottom -= height;
		}
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!isAttachedToWindow()) {
			return false;
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mTouchMode = TOUCH_MODE_DOWN;
			mMotion.mX = ev.getX();
			mMotion.mY = ev.getY();
			mMotion.mPointY = ev.getY();
			mActivePointerId = ev.getPointerId(0);
			startLongPressCheck();
			initOrResetVelocityTracker();
			mVelocityTracker.addMovement(ev);
			break;

		case MotionEvent.ACTION_MOVE:
			int pointerIndex = ev.findPointerIndex(mActivePointerId);
			if (pointerIndex == -1) {
				pointerIndex = 0;
				mActivePointerId = ev.getPointerId(pointerIndex);
			}
			initVelocityTrackerIfNotExists();
			mVelocityTracker.addMovement(ev);
			return startScrollIfNeeded(ev.getX(), ev.getY());

		case MotionEvent.ACTION_UP:
			mActivePointerId = INVALID_POINTER;
			recycleVelocityTracker();
			break;

		default:
			removeCallbacks(mLongPressRunnable);
		}
		return false;
	}

	private void recycleVelocityTracker() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	private boolean startScrollIfNeeded(float x, float y) {
		float deltaY = Math.abs(y - mMotion.mY);
		float deltaX = Math.abs(x - mMotion.mX);
		if (deltaY > mTouchSlop || deltaX > mTouchSlop) {
			removeCallbacks(mLongPressRunnable);
			if (deltaY > deltaX)
				return true;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (getChildCount() == 0)
			return super.onTouchEvent(ev);

		initVelocityTrackerIfNotExists();
		mVelocityTracker.addMovement(ev);

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onTouchDown(ev);
			break;

		case MotionEvent.ACTION_MOVE:
			onTouchMove(ev);
			break;

		case MotionEvent.ACTION_UP:
			onTouchUp(ev);
			break;
		}

		return true;
	}

	private void onTouchUp(MotionEvent ev) {
		removeCallbacks(mLongPressRunnable);
		switch (mTouchMode) {
		case TOUCH_MODE_SCROLL:
			final int childCount = getChildCount();
			if (childCount > 0) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

				final int initialVelocity = (int) (velocityTracker.getYVelocity(mActivePointerId) * mVelocityScale);

				if (Math.abs(initialVelocity) > mMinimumVelocity) {
					if (mFlingRunnable == null) {
						mFlingRunnable = new FlingRunnable();
					}
					mFlingRunnable.start(-initialVelocity);
				} else {
					mTouchMode = TOUCH_MODE_REST;
					if (mFlingRunnable != null) {
						mFlingRunnable.endFling();
					}
				}

			} else {
				mTouchMode = TOUCH_MODE_REST;
			}
			mActivePointerId = INVALID_POINTER;
			break;
		}
	}

	private void onTouchMove(MotionEvent ev) {
		int pointerIndex = ev.findPointerIndex(mActivePointerId);
		if (pointerIndex == -1) {
			pointerIndex = 0;
			mActivePointerId = ev.getPointerId(pointerIndex);
		}
		if (startScrollIfNeeded(ev.getX(), ev.getY())) {
			mTouchMode = TOUCH_MODE_SCROLL;
			mScrolledDistance = ev.getY() - mMotion.mPointY;
			requestLayout();
		}
		mMotion.mPointY = ev.getY();
	}

	private void onTouchDown(MotionEvent ev) {
		mActivePointerId = ev.getPointerId(0);
		mMotion.mPointY = ev.getY();
	}

	/**
	 * 开启异步线程，条件允许时调用LongClickListener
	 */
	private void startLongPressCheck() {
		// 创建子线程
		if (mLongPressRunnable == null) {
			mLongPressRunnable = new Runnable() {

				@Override
				public void run() {
					if (mTouchMode == TOUCH_MODE_DOWN) {
						int index = getContainingChildIndex((int) mMotion.mX, (int) mMotion.mY);
						if (index != INVALID_POSITION) {
							longClickChild(index);
						}
					}
				}
			};
		}
		postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
	}

	private void longClickChild(int index) {
		View itemView = getChildAt(index);
		int position = mFirstItemPosition + index;
		long id = mAdapter.getItemId(position);
		OnItemLongClickListener listener = getOnItemLongClickListener();

		if (listener != null) {
			listener.onItemLongClick(this, itemView, position, id);
		}
	}

	private int getContainingChildIndex(int x, int y) {
		Rect mRect = new Rect();
		// 遍历当前ListView所有Item
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).getHitRect(mRect);
			// x,y是否在当前视图区域内
			if (mRect.contains(x, y)) {
				return i;
			}
		}

		return INVALID_POSITION;
	}

	private void initOrResetVelocityTracker() {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		} else {
			mVelocityTracker.clear();
		}
	}

	private void initVelocityTrackerIfNotExists() {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
	}

	private class FlingRunnable implements Runnable {
		private final OverScroller mScroller;

		private int mLastFlingY;

		private final Runnable mCheckFlywheel = new Runnable() {
			@Override
			public void run() {
			}
		};

		private static final int FLYWHEEL_TIMEOUT = 40; // milliseconds

		FlingRunnable() {
			mScroller = new OverScroller(getContext());
		}

		void start(int initialVelocity) {
			int initialY = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
			mLastFlingY = initialY;
			mScroller.fling(0, initialY, 0, initialVelocity, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
			mTouchMode = TOUCH_MODE_FLING;
			postOnAnimation(this);
		}

		void endFling() {
			mTouchMode = TOUCH_MODE_REST;

			removeCallbacks(this);
			removeCallbacks(mCheckFlywheel);
		}

		void flywheelTouch() {
			postDelayed(mCheckFlywheel, FLYWHEEL_TIMEOUT);
		}

		@Override
		public void run() {
			switch (mTouchMode) {
			default:
				endFling();
				return;

			case TOUCH_MODE_SCROLL:
				if (mScroller.isFinished()) {
					return;
				}
				// Fall through
			case TOUCH_MODE_FLING:
				if (getChildCount() == 0) {
					endFling();
					return;
				}

				final OverScroller scroller = mScroller;
				boolean more = scroller.computeScrollOffset();
				boolean atEnd = false;
				final int y = scroller.getCurrY();

				mScrolledDistance = mLastFlingY - y;

				requestLayout();

				if (more && !atEnd) {
					mLastFlingY = y;
					postOnAnimation(this);
				} else {
					endFling();
				}
				break;
			}
		}
	}
}
