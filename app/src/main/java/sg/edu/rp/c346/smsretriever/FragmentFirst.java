package sg.edu.rp.c346.smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFirst extends Fragment {

    EditText etInput;
    Button btnRetrieve,btnEmail;
    TextView tvInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        btnRetrieve = view.findViewById(R.id.btnGetNum);
        etInput = view.findViewById(R.id.etNumber);
        tvInfo = view.findViewById(R.id.tvRetrieve);
        btnEmail = view.findViewById(R.id.btnEmail);

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, 0);
                    return;
                }

                Uri uri = Uri.parse("content://sms");

                String[] reqCols = new String[]{"date","address","body","type"};

                ContentResolver cr = getActivity().getContentResolver();

                String filter = "address LIKE ? AND type LIKE ?";

                String[] filterArgs = {"%" + etInput.getText().toString() + "%", "1"};

                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
                String smsBody = "";
                if (cursor.moveToFirst()){
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvInfo.setText(smsBody);
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"kaibryanchin@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Test Email");
                emailIntent.putExtra(Intent.EXTRA_TEXT, tvInfo.getText().toString());

                startActivity(emailIntent);
            }
        });

        return view;
    }
}
