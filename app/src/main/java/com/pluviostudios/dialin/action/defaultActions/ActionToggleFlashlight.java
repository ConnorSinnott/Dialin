package com.pluviostudios.dialin.action.defaultActions;

import android.hardware.Camera;

import com.pluviostudios.dialin.R;
import com.pluviostudios.dialin.action.Action;
import com.pluviostudios.dialin.action.ConfigurationFragment;
import com.pluviostudios.dialin.action.DialinImage;

/**
 * Created by spectre on 7/31/16.
 */
public class ActionToggleFlashlight extends Action {

    public static final String TAG = "ActionToggleFlashlight";

    private static Camera sCamera;

    @Override
    public int getActionId() {
        return 0;
    }

    @Override
    public String getActionName() {
        return "Toggle Flashlight";
    }

    @Override
    public DialinImage getActionImage() {
        return new DialinImage(getContext(), R.drawable.flash);
    }

    @Override
    public boolean onExecute() {

        if (sCamera == null)
            sCamera = Camera.open();

        Camera.Parameters p = sCamera.getParameters();

        if (!p.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            sCamera.setParameters(p);
            sCamera.startPreview();
        } else {
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            sCamera.stopPreview();
            sCamera.release();
            sCamera = null;
        }

        return true;

    }

    @Override
    public ConfigurationFragment buildConfigurationFragment() {
        return null;
    }

}
