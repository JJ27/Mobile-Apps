package com.example.listviewprojectb.databinding;
import com.example.listviewprojectb.R;
import com.example.listviewprojectb.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityMainBindingLandImpl extends ActivityMainBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.llistView, 1);
        sViewsWithIds.put(R.id.view2, 2);
        sViewsWithIds.put(R.id.view, 3);
        sViewsWithIds.put(R.id.view1, 4);
        sViewsWithIds.put(R.id.view5, 5);
        sViewsWithIds.put(R.id.view3, 6);
        sViewsWithIds.put(R.id.ytPlayer, 7);
        sViewsWithIds.put(R.id.electtitle, 8);
        sViewsWithIds.put(R.id.lastelec, 9);
        sViewsWithIds.put(R.id.opinion, 10);
        sViewsWithIds.put(R.id.volup, 11);
        sViewsWithIds.put(R.id.watch, 12);
        sViewsWithIds.put(R.id.voldown, 13);
        sViewsWithIds.put(R.id.filter, 14);
        sViewsWithIds.put(R.id.lelectionyear, 15);
        sViewsWithIds.put(R.id.lcomposition, 16);
        sViewsWithIds.put(R.id.lstate, 17);
        sViewsWithIds.put(R.id.filter2, 18);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityMainBindingLandImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 19, sIncludes, sViewsWithIds));
    }
    private ActivityMainBindingLandImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , null
            , null
            , (android.widget.TextView) bindings[8]
            , (android.widget.Spinner) bindings[14]
            , (android.widget.Spinner) bindings[18]
            , (android.widget.TextView) bindings[9]
            , (android.widget.TextView) bindings[16]
            , (android.widget.TextView) bindings[15]
            , null
            , (android.widget.ListView) bindings[1]
            , (android.widget.TextView) bindings[17]
            , (android.widget.TextView) bindings[10]
            , null
            , (android.view.View) bindings[3]
            , (android.view.View) bindings[4]
            , (android.view.View) bindings[2]
            , (android.view.View) bindings[6]
            , (android.view.View) bindings[5]
            , (android.widget.Button) bindings[13]
            , (android.widget.Button) bindings[11]
            , (android.widget.Button) bindings[12]
            , (com.google.android.youtube.player.YouTubePlayerView) bindings[7]
            );
        this.mboundView0 = (androidx.constraintlayout.widget.ConstraintLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}