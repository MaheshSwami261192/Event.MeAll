package com.prometteur.myevents.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextCustomFont extends EditText {

    public EditTextCustomFont(Context context) {
        super(context);
        initCustomEditText(context);
    }

    public EditTextCustomFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCustomEditText(context);
    }

    public EditTextCustomFont(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCustomEditText(context);
    }

    public EditTextCustomFont(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCustomEditText(context);
    }

    private void initCustomEditText(Context context)
    {
       /* if(!isInEditMode())
        {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/poppins_light.ttf");
//            Typeface typeface = Typeface.createFromFile("font/poppins_light.ttf");//.createFromAsset(context, "poppins_light.ttf");
            setTypeface(typeface);
        }*/
    }
}
