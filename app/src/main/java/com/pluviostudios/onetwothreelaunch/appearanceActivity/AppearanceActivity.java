package com.pluviostudios.onetwothreelaunch.appearanceActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pluviostudios.onetwothreelaunch.R;
import com.pluviostudios.onetwothreelaunch.buttonSkins.AppearanceItem;
import com.pluviostudios.onetwothreelaunch.buttonSkins.AppearanceManager;
import com.pluviostudios.onetwothreelaunch.buttonSkins.HighlightItem;
import com.pluviostudios.onetwothreelaunch.buttonSkins.SkinSetItem;
import com.pluviostudios.onetwothreelaunch.dialogFragments.IconListDialogFragment;
import com.pluviostudios.onetwothreelaunch.dialogFragments.IconListDialogFragmentEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.pluviostudios.onetwothreelaunch.action.ActionManager.getContext;

/**
 * Created by spectre on 8/13/16.
 */
public class AppearanceActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "AppearanceActivity";

    public static final int APPEARANCE_ACTIVITY_REQUEST_CODE = 555;
    public static final String EXTRA_CHANGES_MADE = "extra_changes_made";

    private static final int HIGHLIGHT_REQUEST_CODE = 5610;
    private static final int SKIN_SET_REQUEST_CODE = 9570;

    private Button mButtonSave;

    private View mListItemHighlight;
    private ImageView mHighlightImageView;
    private TextView mHighlightTextView;

    private View mListItemSkinSet;
    private ImageView mSkinSetImageView;
    private TextView mSkinSetTextView;

    private AppearanceItem mAppearanceItem;

    private boolean changesMade = false;

    private void init() {

        mButtonSave = (Button) findViewById(R.id.activity_appearance_button_save);

        mListItemHighlight = findViewById(R.id.activity_appearance_list_item_highlight);
        mHighlightImageView = (ImageView) mListItemHighlight.findViewById(R.id.list_item_action_image);
        mHighlightTextView = (TextView) mListItemHighlight.findViewById(R.id.list_item_action_text_view);

        mListItemSkinSet = findViewById(R.id.activity_appearance_list_item_skin_set);
        mSkinSetImageView = (ImageView) mListItemSkinSet.findViewById(R.id.list_item_action_image);
        mSkinSetTextView = (TextView) mListItemSkinSet.findViewById(R.id.list_item_action_text_view);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Appearance");
        setContentView(R.layout.activity_appearance);
        init();

        mAppearanceItem = AppearanceManager.getAppearanceItem(getContext());

        updateHighlightItem();
        updateSkinSetItem();

        mListItemHighlight.setOnClickListener(this);
        mListItemSkinSet.setOnClickListener(this);
        mButtonSave.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void updateSkinSetItem() {
        mSkinSetTextView.setText(mAppearanceItem.skinSetItem.title);
        mSkinSetImageView.setImageURI(mAppearanceItem.skinSetItem.buttonIconUris[0]);
    }


    private void updateHighlightItem() {
        mHighlightTextView.setText(mAppearanceItem.highlightItem.title);
        mHighlightImageView.setImageResource(mAppearanceItem.highlightItem.previewResourceId);
    }

    private void showHighlightDialog() {

        IconListDialogFragment.Builder builder = new IconListDialogFragment.Builder(HIGHLIGHT_REQUEST_CODE);
        for (HighlightItem x : AppearanceManager.getHighlightItems(getContext())) {
            builder.addItem(x.title, getContext(), x.previewResourceId);
        }
        builder.build().show(getSupportFragmentManager(), IconListDialogFragment.TAG);

    }

    private void showSkinDialog() {

        IconListDialogFragment.Builder builder = new IconListDialogFragment.Builder(SKIN_SET_REQUEST_CODE);
        for (SkinSetItem x : AppearanceManager.getSkinSets(getContext())) {
            builder.addItem(x.title, x.buttonIconUris[0]);
        }
        builder.build().show(getSupportFragmentManager(), IconListDialogFragment.TAG);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.activity_appearance_button_save: {
                finishActivity();
                break;
            }

            case R.id.activity_appearance_list_item_highlight: {
                showHighlightDialog();
                break;
            }

            case R.id.activity_appearance_list_item_skin_set: {
                showSkinDialog();
                break;
            }

        }
    }

    @Subscribe
    public void onIconListDialogFragmentEvent(IconListDialogFragmentEvent event) {

        changesMade = true;

        switch (event.requestCode) {
            case HIGHLIGHT_REQUEST_CODE: {

                mAppearanceItem.highlightItem = AppearanceManager.getHighlightItems(getContext()).get(event.position);
                updateHighlightItem();

                break;
            }

            case SKIN_SET_REQUEST_CODE: {

                mAppearanceItem.skinSetItem = AppearanceManager.getSkinSets(getContext()).get(event.position);
                updateSkinSetItem();

                break;

            }

        }

    }

    private void finishActivity() {

        if (changesMade) {
            AppearanceManager.setAppearanceItem(this, mAppearanceItem);
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_CHANGES_MADE, changesMade);
        setResult(RESULT_OK, resultIntent);
        finish();

    }


}
