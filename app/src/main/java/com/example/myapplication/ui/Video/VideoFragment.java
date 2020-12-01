package com.example.myapplication.ui.Video;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class VideoFragment extends Fragment {
    private String API_KEY = "AIzaSyAAaQdcl-dL4-6CKH1KtPybdnCeHRiftcE";
    private VideoViewModel homeViewModel;
    private LinearLayout proposal1;
    private LinearLayout proposal2;
    private LinearLayout proposal3;
    private LinearLayout btn_channel1;
    private LinearLayout btn_channel2;
    private LinearLayout btn_channel3;
    private LinearLayout btn_channel4;
    private LinearLayout btn_channel5;
    private LinearLayout btn_channel6;
    private Button btn_search;
    private EditText et_search;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(VideoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_video, container, false);
        proposal1 = root.findViewById(R.id.proposal1);
        proposal2 = root.findViewById(R.id.proposal2);
        proposal3 = root.findViewById(R.id.proposal3);
        btn_channel1 = root.findViewById(R.id.btn_channel1);
        btn_channel2 = root.findViewById(R.id.btn_channel2);
        btn_channel3 = root.findViewById(R.id.btn_channel3);
        btn_channel4 = root.findViewById(R.id.btn_channel4);
        btn_channel5 = root.findViewById(R.id.btn_channel5);
        btn_channel6 = root.findViewById(R.id.btn_channel6);
        btn_search = root.findViewById(R.id.btn_search);
        et_search = root.findViewById(R.id.et_search);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = et_search.getText().toString();
                try {
                    ArrayList<String> result = new YoutubeSearchAsyncTask().execute(text).get();
                    Intent intent = new Intent(v.getContext(),YoutubeListActivity.class);
                    intent.putExtra("videoList",result);
                    startActivity(intent);
                }
                catch (Exception e){

                }
            }
        });
        btn_channel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArrayList<String> result = new YoutubeSearchAsyncTask().execute("Domics channel").get();
                    Intent intent = new Intent(v.getContext(),YoutubeListActivity.class);
                    intent.putExtra("videoList",result);
                    startActivity(intent);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_channel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArrayList<String> result = new YoutubeSearchAsyncTask().execute("Doctor Mike channel").get();
                    Intent intent = new Intent(v.getContext(),YoutubeListActivity.class);
                    intent.putExtra("videoList",result);
                    startActivity(intent);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_channel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArrayList<String> result = new YoutubeSearchAsyncTask().execute("TerryTV channel").get();
                    Intent intent = new Intent(v.getContext(),YoutubeListActivity.class);
                    intent.putExtra("videoList",result);
                    startActivity(intent);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_channel4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArrayList<String> result = new YoutubeSearchAsyncTask().execute("Jenn Im channel").get();
                    Intent intent = new Intent(v.getContext(),YoutubeListActivity.class);
                    intent.putExtra("videoList",result);
                    startActivity(intent);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_channel5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArrayList<String> result = new YoutubeSearchAsyncTask().execute("Nigahiga channel").get();
                    Intent intent = new Intent(v.getContext(),YoutubeListActivity.class);
                    intent.putExtra("videoList",result);
                    startActivity(intent);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_channel6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArrayList<String> result = new YoutubeSearchAsyncTask().execute("Wong Fu Productions channel").get();
                    Intent intent = new Intent(v.getContext(),YoutubeListActivity.class);
                    intent.putExtra("videoList",result);
                    startActivity(intent);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        proposal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String video_id = "CHU-MESkMQI";
                String video_title = "We Bare Bears - Cannot be Parted (Clip) HD with Subs";
                Intent intent1 = new Intent(v.getContext(), YoutubeSelectedActivity.class);
                intent1.putExtra("video_id",video_id);
                intent1.putExtra("video_title",video_title);
                startActivity(intent1);
            }
        });

        proposal2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String video_id = "zk85Zdx4siQ";
                String video_title = "Log Ride | Minisode | We Bare Bears | Cartoon Network";
                Intent intent1 = new Intent(v.getContext(), YoutubeSelectedActivity.class);
                intent1.putExtra("video_id",video_id);
                intent1.putExtra("video_title",video_title);
                startActivity(intent1);
            }
        });

        proposal3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String video_id = "g_k9w-gW12c";
                String video_title = "Panda's Dream | Minisode | We Bare Bears | Cartoon Network";
                Intent intent1 = new Intent(v.getContext(), YoutubeSelectedActivity.class);
                intent1.putExtra("video_id",video_id);
                intent1.putExtra("video_title",video_title);
                startActivity(intent1);
            }
        });

        return root;
    }

    private class YoutubeSearchAsyncTask extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(String... str) {
            String search_text;
            try {
                search_text = URLEncoder.encode(str[0], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("인코딩 실패", e);
            }
            try {
                HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
                final JsonFactory JSON_FACTORY = new JacksonFactory();
                final long NUMBER_OF_VIDEOS_RETURNED = 5;

                YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) throws IOException {
                    }
                }).setApplicationName("youtube-search-sample").build();

                YouTube.Search.List search = youtube.search().list("id,snippet");
                search.setKey(API_KEY);

                search.setQ(search_text);

                search.setOrder("relevance"); //date relevance

                search.setType("video");

                search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url,snippet/channelTitle)");
                search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
                SearchListResponse searchResponse = search.execute();

                List<SearchResult> searchResultList = searchResponse.getItems();

                if (searchResultList != null) {
                    ArrayList<String> result = prettyPrint(searchResultList.iterator(), "레드벨벳");
                    return result;
                }
            } catch (GoogleJsonResponseException e) {
                System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
                System.err.println("There was a service error 2: " + e.getLocalizedMessage() + " , " + e.toString());
            } catch (IOException e) {
                System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> aVoid) {
            super.onPostExecute(aVoid);
        }

        public ArrayList<String> prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {
            if (!iteratorSearchResults.hasNext()) {
                System.out.println(" There aren't any results for your query.");
            }

            ArrayList<String> VideoInfo = new ArrayList<String>();

            while (iteratorSearchResults.hasNext()) {
                SearchResult singleVideo = iteratorSearchResults.next();
                ResourceId rId = singleVideo.getId();

                // Double checks the kind is video.
                if (rId.getKind().equals("youtube#video")) {
                    Thumbnail thumbnail = (Thumbnail) singleVideo.getSnippet().getThumbnails().get("default");

                    VideoInfo.add(rId.getVideoId());
                    VideoInfo.add(singleVideo.getSnippet().getTitle());
                    VideoInfo.add(thumbnail.getUrl());
                    VideoInfo.add(singleVideo.getSnippet().getChannelTitle());
                }
            }

            return VideoInfo;
        }
    }
}
