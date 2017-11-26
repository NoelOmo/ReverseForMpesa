package ke.co.the_noel.reverseformpesa;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by root on 4/8/17.
 */

public class ReverseConfirmationDialog extends DialogFragment {

    TextView txtSMSBody;
    Bundle bundle;

    public ReverseConfirmationDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.reverse_confirmation_dialog, container);
       bundle = getArguments();
       txtSMSBody = view.findViewById(R.id.sms_body);
       txtSMSBody.setText(bundle.getString("text_msg"));
       getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
