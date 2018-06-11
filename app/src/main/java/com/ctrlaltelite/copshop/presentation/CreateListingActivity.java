package com.ctrlaltelite.copshop.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ctrlaltelite.copshop.R;
import com.ctrlaltelite.copshop.logic.CopShopApp;
import com.ctrlaltelite.copshop.objects.ListingFormValidationObject;
import com.ctrlaltelite.copshop.objects.ListingObject;

public class CreateListingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button submitButton = (Button) findViewById(R.id.btnCreateListing);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SubmitButton", "Clicked!");
                //create

                ListingObject listingObject = new ListingObject(
                        "ignored",
                        ((EditText) findViewById(R.id.txtListingTitle)).getText().toString(),
                        ((EditText) findViewById(R.id.txtAreaDescription)).getText().toString(),
                        ((EditText) findViewById(R.id.txtInitialPrice)).getText().toString(),
                        ((EditText) findViewById(R.id.txtMinBid)).getText().toString(),
                        ((EditText) findViewById(R.id.txtStartDay)).getText().toString() + "/" + ((EditText) findViewById(R.id.txtStartMonth)).getText().toString() + "/" + ((EditText) findViewById(R.id.txtStartYear)).getText().toString(),
                        ((EditText) findViewById(R.id.txtStartTimeHour)).getText().toString() + ":" + ((EditText) findViewById(R.id.txtStartTimeMinute)).getText().toString(),
                        ((EditText) findViewById(R.id.txtEndDay)).getText().toString() + "/" +  ((EditText) findViewById(R.id.txtEndMonth)).getText().toString() + "/" + ((EditText) findViewById(R.id.txtEndYear)).getText().toString(),
                        ((EditText) findViewById(R.id.txtEndTimeHour)).getText().toString() + ":" + ((EditText) findViewById(R.id.txtEndTimeMinute)).getText().toString(),
                        "1" // TODO: currently logged in seller's id
                );

                ListingFormValidationObject validationObject = CopShopApp.createListingService.create(listingObject);

                //check validation object to see if all fields are valid
                //if valid: store form data in listing database
                //else invalid: check each form field, highlighting those that are invalid in red
                if (validationObject.isAllValid()) {
                    CopShopApp.createListingService.saveNewListing(listingObject);

                    // Goto listing list page
                    startActivity(new Intent(CreateListingActivity.this, ListingListActivity.class));
                } else {

                    //check listing title
                    if (!validationObject.getTitleValid()) {
                        findViewById(R.id.txtListingTitle).setBackgroundResource(R.drawable.txt_field_red_border);
                    } else {
                        findViewById(R.id.txtListingTitle).setBackgroundResource(R.drawable.txt_field_black_border);
                    }

                    //check listing initial price
                    if (!validationObject.getInitPriceValid()) {
                        findViewById(R.id.txtInitialPrice).setBackgroundResource(R.drawable.txt_field_red_border);
                    } else {
                        findViewById(R.id.txtInitialPrice).setBackgroundResource(R.drawable.txt_field_black_border);
                    }

                    //check listing minimum bet increase amount
                    if (!validationObject.getMinBidValid()) {
                        findViewById(R.id.txtMinBid).setBackgroundResource(R.drawable.txt_field_red_border);
                    } else {
                        findViewById(R.id.txtMinBid).setBackgroundResource(R.drawable.txt_field_black_border);
                    }

                    //check all fields relating to listing start date and time
                    if (!validationObject.getStartDateAndTimeValid()) {
                        findViewById(R.id.txtStartDay).setBackgroundResource(R.drawable.txt_field_red_border);
                        findViewById(R.id.txtStartMonth).setBackgroundResource(R.drawable.txt_field_red_border);
                        findViewById(R.id.txtStartYear).setBackgroundResource(R.drawable.txt_field_red_border);
                        findViewById(R.id.txtStartTimeHour).setBackgroundResource(R.drawable.txt_field_red_border);
                        findViewById(R.id.txtStartTimeMinute).setBackgroundResource(R.drawable.txt_field_red_border);
                    } else {
                        findViewById(R.id.txtStartDay).setBackgroundResource(R.drawable.txt_field_black_border);
                        findViewById(R.id.txtStartMonth).setBackgroundResource(R.drawable.txt_field_black_border);
                        findViewById(R.id.txtStartYear).setBackgroundResource(R.drawable.txt_field_black_border);
                        findViewById(R.id.txtStartTimeHour).setBackgroundResource(R.drawable.txt_field_black_border);
                        findViewById(R.id.txtStartTimeMinute).setBackgroundResource(R.drawable.txt_field_black_border);
                    }

                    //check all fields relating to listing end date and time
                    if (!validationObject.getEndDateAndTimeValid()) {
                        findViewById(R.id.txtEndDay).setBackgroundResource(R.drawable.txt_field_red_border);
                        findViewById(R.id.txtEndMonth).setBackgroundResource(R.drawable.txt_field_red_border);
                        findViewById(R.id.txtEndYear).setBackgroundResource(R.drawable.txt_field_red_border);
                        findViewById(R.id.txtEndTimeHour).setBackgroundResource(R.drawable.txt_field_red_border);
                        findViewById(R.id.txtEndTimeMinute).setBackgroundResource(R.drawable.txt_field_red_border);
                    } else {
                        findViewById(R.id.txtEndDay).setBackgroundResource(R.drawable.txt_field_black_border);
                        findViewById(R.id.txtEndMonth).setBackgroundResource(R.drawable.txt_field_black_border);
                        findViewById(R.id.txtEndYear).setBackgroundResource(R.drawable.txt_field_black_border);
                        findViewById(R.id.txtEndTimeHour).setBackgroundResource(R.drawable.txt_field_black_border);
                        findViewById(R.id.txtEndTimeMinute).setBackgroundResource(R.drawable.txt_field_black_border);
                    }

                    //check listing description
                    if (!validationObject.getDescriptionValid()) {
                        findViewById(R.id.txtAreaDescription).setBackgroundResource(R.drawable.txt_field_red_border);
                    } else {
                        findViewById(R.id.txtAreaDescription).setBackgroundResource(R.drawable.txt_field_black_border);
                    }
                }
            }
        });
    }
}