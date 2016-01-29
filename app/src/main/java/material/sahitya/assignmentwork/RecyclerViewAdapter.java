package material.sahitya.assignmentwork;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter {

    private static final String APSSDOME_API = "http://viralwoot.com/api/test.php";

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private static final int NOTIFY_ME_ID = 1337;

    private List<UserDetails> userDetailsList;

    private int threshold = 5;
    private int lastItem, totalitem;
    private boolean loading;

    private InfiniteScrollListener onLoadMoreListener;
    private ProgressDialog pDialog;

    private Context _context;
    private String _userIdRequest;
    private String _userKeyRequest;


    public RecyclerViewAdapter(List<UserDetails> detailsList, RecyclerView recyclerView) {
        userDetailsList = detailsList;
        _context = recyclerView.getContext();

        recyclerView.setItemAnimator(new DefaultItemAnimator() {

            @Override
            public boolean animateRemove(RecyclerView.ViewHolder holder) {
                Animation animation;
                animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_animation);
                holder.itemView.setAnimation(animation);
                return super.animateRemove(holder);
            }

            @Override
            public boolean animateAdd(RecyclerView.ViewHolder holder) {
                return super.animateAdd(holder);
            }
        });

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();


            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalitem = linearLayoutManager.getItemCount();
                    lastItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalitem <= (lastItem + threshold)) {

                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return userDetailsList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.recyclerview_list_row_item, parent, false);

            vh = new UserViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progress_bar_waiting_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof UserViewHolder) {

            final UserDetails userDetails = (UserDetails) userDetailsList.get(position);

            ((UserViewHolder) holder).userName.setText(userDetails.get_userName());

            ((UserViewHolder) holder).userKey.setText(userDetails.get_userKey());
            ((UserViewHolder) holder).userId.setText(userDetails.get_userId());
            ((UserViewHolder) holder).details = userDetails;
            ((UserViewHolder) holder).userFollowLButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new GetApiData().execute();

                    deleteItemSelected(position);
                    set_userIdRequest(userDetails.get_userId());
                    setRequestUserkey(userDetails.get_userKey());
                    Toast.makeText(v.getContext(), "Clicked on" + position, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            ((ProgressViewHolder) holder).waiting_progressbar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return userDetailsList.size();
    }

    public void setOnLoadMoreListener(InfiniteScrollListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    //
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, userId, userKey;
        public Button userFollowLButton;
        public UserDetails details;

        public UserViewHolder(View v) {
            super(v);
            userName = (TextView) v.findViewById(R.id.user_name);
            userId = (TextView) v.findViewById(R.id.user_id);
            userKey = (TextView) v.findViewById(R.id.key_id);
            userFollowLButton = (Button) v.findViewById(R.id.follow_button);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar waiting_progressbar;

        public ProgressViewHolder(View v) {
            super(v);
            waiting_progressbar = (ProgressBar) v.findViewById(R.id.progress_waiting);
        }
    }

    public void deleteItemSelected(int position) {
        userDetailsList.remove(position);
        notifyItemRangeRemoved(position, 1);

    }

    private class GetApiData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(_context);
            pDialog.setMessage("Please Wait");
            pDialog.setTitle("Loading...");
            showpDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String tag_string_req = "appDome_api_request";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, APSSDOME_API, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        String data = jsonArray.getString(0);
                        String data1 = jsonArray.getString(1);
                        Toast.makeText(_context, "Success " + data + "Points" + data1, Toast.LENGTH_SHORT).show();
                        if (data.contentEquals("success")) {
                            long[] vibrate = {0, 100, 200, 300};
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(_context);
                            builder.setSmallIcon(R.mipmap.ic_launcher);
                            Intent intent = new Intent(_context, MainActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(_context, 0, intent, 0);
                            Uri notifySound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            builder.setContentIntent(pendingIntent);
                            builder.setLargeIcon(BitmapFactory.decodeResource(_context.getResources(), R.drawable.noti));
                            builder.setContentTitle("AppsDome with " + data);
                            builder.setContentText("Congratulation you have earned " + data1 + "+points");
                            builder.setSubText("You made it at last, Best of luck with your money!");
                            builder.setSound(notifySound);
                            builder.setVibrate(vibrate);
                            NotificationManager notificationManager = (NotificationManager) _context.getSystemService(_context.NOTIFICATION_SERVICE);
                            notificationManager.notify(NOTIFY_ME_ID, builder.build());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        hidepDialog();
                    }
                }


            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Adapter Volley Request Error", "Error: " + error.getMessage());
                    hidepDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userid", getUSerId());
                    params.put("targetid", getUserKey());
                    return params;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hidepDialog();
        }
    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private void set_userIdRequest(String _userIdRequest) {
        this._userIdRequest = _userIdRequest;
    }

    private void setRequestUserkey(String requestUserkey) {
        this._userKeyRequest = requestUserkey;
    }

    private String getUSerId() {
        return this._userIdRequest;
    }

    private String getUserKey() {
        return this._userKeyRequest;
    }

}