package ca.vijaysharma.materialdesignicons.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements
        SearchView.OnQueryTextListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String COLOR_PREFERENCE_KEY = "color_preference_key";
    private static final String DEFAULT_BACKGROUND_COLOR = "#E8E8E8";

    private ListView list;
    private Adapter adapter;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        adapter = new Adapter(this, preferences);
        list = (ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService( Context.SEARCH_SERVICE );
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_item_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_color:
                DialogFragment dialog = new HexColorDialog();
                dialog.show(getFragmentManager(), "HexColorDialog");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        adapter.notifyDataSetChanged();
    }

    private static int colorFromString(String colorValue) {
        try {
            return Color.parseColor(colorValue);
        } catch (Exception ex) {
            return Color.parseColor(DEFAULT_BACKGROUND_COLOR);
        }
    }

    private static class IconRowItem {
        private final ImageView i48dp;
        private final ImageView i36dp;
        private final ImageView i24dp;
        private final ImageView i18dp;
        private final TextView label;

        private IconRowItem(View view) {
            i48dp = (ImageView) view.findViewById(R.id.i48);
            i36dp = (ImageView) view.findViewById(R.id.i36);
            i24dp = (ImageView) view.findViewById(R.id.i24);
            i18dp = (ImageView) view.findViewById(R.id.i18);
            label = (TextView) view.findViewById(R.id.label);
        }
    }

    private static class Adapter extends BaseAdapter {
        private final LayoutInflater inflater;
        private final Context context;
        private final SharedPreferences preferences;
        private final List<String> data;

        public Adapter(Context context, SharedPreferences preferences) {
            this.context = context;
            this.preferences = preferences;
            this.inflater = LayoutInflater.from(context);
            this.data = new ArrayList<>(Icons.data);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public final View getView(int position, View view, ViewGroup container) {
            if (view == null) {
                view = newView(inflater, position, container);
                if (view == null) {
                    throw new IllegalStateException("newView result must not be null.");
                }
            }
            bindView(getItem(position), position, view);
            return view;
        }

        public void filter(String value) {
            this.data.clear();
            if (TextUtils.isEmpty(value)) {
                this.data.addAll(Icons.data);
            } else {
                for (String item : Icons.data) {
                    if (item.contains(value)) {
                        this.data.add(item);
                    }
                }
            }

            notifyDataSetChanged();
        }

        private View newView(LayoutInflater inflater, int position, ViewGroup container) {
            return inflater.inflate(R.layout.item_icon, container, false);
        }

        private void bindView(String value, int position, View view) {
            IconRowItem holder = (IconRowItem) view.getTag();
            if (holder == null) {
                holder = new IconRowItem(view);
                view.setTag(holder);
            }

            view.setBackgroundColor(getColor());

            holder.i48dp.setImageResource(getResource(value, "48"));
            holder.i36dp.setImageResource(getResource(value, "36"));
            holder.i24dp.setImageResource(getResource(value, "24"));
            holder.i18dp.setImageResource(getResource(value, "18"));
            holder.label.setText(value);
        }

        private int getResource(String resource, String size) {
            return context.getResources().getIdentifier(
                resource + "_" + size + "dp",
                "drawable",
                context.getPackageName()
            );
        }

        private int getColor() {
            String color = preferences.getString(COLOR_PREFERENCE_KEY, DEFAULT_BACKGROUND_COLOR);
            return colorFromString(color);
        }
    }

    public static class HexColorDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            String color = preferences.getString(COLOR_PREFERENCE_KEY, DEFAULT_BACKGROUND_COLOR);

            View view = inflater.inflate(R.layout.dialog_colorpicker, null);
            final View colorBlock = view.findViewById(R.id.color_block);
            colorBlock.setBackgroundColor(MainActivity.colorFromString(color));

            final EditText colorValue = (EditText) view.findViewById(R.id.color_value);
            colorValue.setText(color);
            colorValue.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void afterTextChanged(Editable s) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int color = MainActivity.colorFromString(s.toString());
                    colorBlock.setBackgroundColor(color);
                }
            });

            return builder
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        preferences
                            .edit()
                            .putString(COLOR_PREFERENCE_KEY, colorValue.getText().toString())
                            .apply();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                }).create();
        }
    }
}
