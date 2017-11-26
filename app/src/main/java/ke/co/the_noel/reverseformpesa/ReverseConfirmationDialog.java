package ke.co.the_noel.reverseformpesa;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by root on 4/8/17.
 */

public class ReverseConfirmationDialog extends DialogFragment {

    TextView txtSMSBody;
    Bundle bundle;
    ReverseIt mCallback;
    Button btnReverse;

    public ReverseConfirmationDialog() {
    }

    public interface ReverseIt{
        void handleReverseButtonClick();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.reverse_confirmation_dialog, container);
       btnReverse = view.findViewById(R.id.btn_dialog_reverse);
       bundle = getArguments();
       txtSMSBody = view.findViewById(R.id.sms_body);
       txtSMSBody.setText(bundle.getString("text_msg"));
       getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

       btnReverse.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               mCallback.handleReverseButtonClick();
               getDialog().dismiss();
           }
       });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (ReverseIt) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ReverseIt");
        }
    }
}
