package com.example.mimiss.gdpr_;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ConsentForm form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConsentInformation consentInformation = ConsentInformation.getInstance(this);
        String[] publisherIds = {"pub-0123456789012345"};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                boolean isEEA = ConsentInformation.getInstance(getApplicationContext()).isRequestLocationInEeaOrUnknown();
                if (isEEA) {
                    if(consentStatus == ConsentStatus.NON_PERSONALIZED) {
                        Bundle extras = new Bundle();
                        extras.putString("npa", "1");

                        AdRequest request = new AdRequest.Builder()
                                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                .build();
                    }else if(consentStatus==ConsentStatus.UNKNOWN){
                        URL privacyUrl = null;
                        try {
                            // TODO: Replace with your app's privacy policy URL.
                            privacyUrl = new URL("https://www.your.com/privacyurl");
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            // Handle error.
                        }
                        form = new ConsentForm.Builder(MainActivity.this, privacyUrl)
                                .withListener(new ConsentFormListener() {
                                    @Override
                                    public void onConsentFormLoaded() {
                                        // Consent form loaded successfully.
                                        form.show();
                                    }

                                    @Override
                                    public void onConsentFormOpened() {
                                        // Consent form was displayed.
                                    }

                                    @Override
                                    public void onConsentFormClosed(
                                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                                        // Consent form was closed.
                                        if(consentStatus == ConsentStatus.NON_PERSONALIZED) {
                                            Bundle extras = new Bundle();
                                            extras.putString("npa", "1");

                                            AdRequest request = new AdRequest.Builder()
                                                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                                    .build();
                                        }
                                    }

                                    @Override
                                    public void onConsentFormError(String errorDescription) {
                                        // Consent form error.
                                    }
                                })
                                .withPersonalizedAdsOption()
                                .withNonPersonalizedAdsOption()
                                .withAdFreeOption()
                                .build();
                        form.load();
                    }
                } else {



                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });
    }


}
