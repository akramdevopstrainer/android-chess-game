package jwtc.android.chess.play;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;


import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import jwtc.android.chess.R;
import jwtc.android.chess.helpers.Clipboard;
import jwtc.android.chess.services.GameApi;
import jwtc.chess.JNI;
import jwtc.chess.PGNEntry;

public class PGNDialog extends Dialog {
    private static final String TAG = "PGNDialog";

    public PGNDialog(@NonNull final Context context, final GameApi gameApi) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.full_pgn);

        ArrayList<HashMap<String, String>> mapMoves = new ArrayList<HashMap<String, String>>();
        SimpleAdapter adapterMoves = new SimpleAdapter(context, mapMoves, R.layout.pgn_item,
                new String[]{"nr", "move", "annotation"}, new int[]{R.id.TextViewNumMove, R.id.TextViewMove, R.id.TextViewAnnotation});

        GridView contentLayout = findViewById(R.id.LayoutContent);

        contentLayout.setAdapter(adapterMoves);

        ArrayList<PGNEntry> pgnEntries = gameApi.getPGNEntries();

        for (int i = 0; i < pgnEntries.size(); i++) {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("nr", i % 2 == 0 ? ((i + 1) + ". ") : " ");
            item.put("move", pgnEntries.get(i)._sMove);
            item.put("annotation", pgnEntries.get(i)._sAnnotation);

            mapMoves.add(item);
        }

        adapterMoves.notifyDataSetChanged();

        contentLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (JNI.getInstance().getNumBoard() - 1 > position) {
                    position++;
                }
                gameApi.jumptoMove(position + 1);
                dismiss();
            }
        });

        ImageButton buttonClip = findViewById(R.id.ButtonClip);
        buttonClip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Clipboard.stringToClipboard(context, gameApi.exportFullPGN(), context.getString(R.string.copied_clipboard_success));
                dismiss();
            }
        });

        Button buttonClose = findViewById(R.id.ButtonClose);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}