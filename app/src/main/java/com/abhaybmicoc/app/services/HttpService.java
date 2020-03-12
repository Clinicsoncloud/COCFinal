package com.abhaybmicoc.app.services;

import java.util.Map;

import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.app.ProgressDialog;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.RetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.abhaybmicoc.app.interfaces.VolleyResponse;
import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

public class HttpService {

    public static void accessWebServices(final Context context, String url, final Map param, final Map headerParam, final VolleyResponse responseListner) {
        final ProgressDialog loading = ProgressDialog.show(context, "Loading.....", "Please wait...", true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("onResponse", "" + response);
                        if (!((Activity) context).isFinishing()) {
                            loading.dismiss();
                        }
                        responseListner.onProcessFinish(response, null, "response");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("onErrorResponse", "" + error);
                        responseListner.onProcessFinish("", error, "error");

                        if (loading != null && loading.isShowing()) {
                            loading.dismiss();
//                        if (loading != null)

                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return param;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headerParam;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {

                Log.e("getCurrentTimeout", "" + "5000");

                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                Log.e("getCurrentRetryCount", "" + "5000");

                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                //Toast.makeText(context, "" + error, Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse", "" + error);
//                responseListner.onError(error);
                responseListner.onProcessFinish("", error, "error");
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public static void accessWebServicesNoDialog(final Context context, String url, final Map param, final Map headerParam, final VolleyResponse responseListner) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        responseListner.onProcessFinish(response, null, "response");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseListner.onProcessFinish("", error, "error");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return param;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headerParam;
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                responseListner.onProcessFinish("", error, "error");
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public static void accessWebServicesJSON(final Context context, String url, final JSONObject jsonObject,
                                             final VolleyResponse responseListner) {
        final ProgressDialog loading = ProgressDialog.show(context, "Loading.....", "Please wait...", true);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("onResponse", "" + response);
                        if (!((Activity) context).isFinishing()) {
                            loading.dismiss();
                        }
                        responseListner.onProcessFinish(String.valueOf(response), null, "response");

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", "" + error);
                responseListner.onProcessFinish("", error, "error");

                if (loading != null && loading.isShowing()) {
                    loading.dismiss();
//                        if (loading != null)

                }

            }
        }) {
            protected JSONObject getJsonObject() {
                return jsonObject;
            }
        };

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                //Toast.makeText(context, "" + error, Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse", "" + error);
                responseListner.onProcessFinish("", error, "error");
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }


    public static void accessWebServicesJSONNoDialog(final Context context, String url, final JSONObject jsonObject,
                                                     final VolleyResponse responseListner) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("onResponse", "" + response);
                        responseListner.onProcessFinish(String.valueOf(response), null, "response");

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", "" + error);
                responseListner.onProcessFinish("", error, "error");

            }
        }) {
            protected JSONObject getJsonObject() {
                return jsonObject;
            }
        };

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                //Toast.makeText(context, "" + error, Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse", "" + error);
                responseListner.onProcessFinish("", error, "error");
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }


}

