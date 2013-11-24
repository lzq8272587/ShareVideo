package com.lzq.sharevideo;



import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

public class StreamingFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
        View v = inflater.inflate(R.layout.streaming_fragment, container, false);
        VideoView vv = (VideoView)(v.findViewById(R.id.StreamingMediaVideoView));
        Button start=(Button)(v.findViewById(R.id.StartStreaming));
        
        return v;  
		
	}

	
}
