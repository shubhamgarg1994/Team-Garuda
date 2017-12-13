package org.gcs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView.BufferType;
import android.widget.Spinner;
import net.bgreco.*;
import org.gcs.R;

public class CameraSettings extends Activity {
	
	Button settings_done;
	Button change_path;
	
	EditText pathET;
	
	EditText address1_input;
	EditText address2_input;
	EditText address3_input;
	EditText address4_input;
	EditText port_input;
	EditText command_input;

	Button address1_increment;
	Button address2_increment;
	Button address3_increment;
	Button address4_increment;
	
	Button address1_decrement;	
	Button address2_decrement;	
	Button address3_decrement;	
	Button address4_decrement;	
	
	RadioGroup port_group;
	RadioGroup command_group;
	
	int width = 640;
	int height = 480;
	
	int ip_ad1 = 192;
	int ip_ad2 = 168;
	int ip_ad3 = 1;
	int ip_ad4 = 102;
	int ip_port = 8090;
	String ip_command = "?action=stream";
	String path=Environment.getDataDirectory().toString();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_settings);
        
        Bundle extras = getIntent().getExtras();
		
        address1_input = (EditText) findViewById(R.id.address1_input);
        address2_input = (EditText) findViewById(R.id.address2_input);
        address3_input = (EditText) findViewById(R.id.address3_input);
        address4_input = (EditText) findViewById(R.id.address4_input);
        port_input = (EditText) findViewById(R.id.port_input);
        command_input = (EditText) findViewById(R.id.command_input);
        
        change_path = (Button) findViewById(R.id.button1);
        pathET = (EditText) findViewById(R.id.EditText01);
        pathET.setText(path);
        
        change_path.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CameraSettings.this, DirectoryPicker.class);
				// optionally set options here
				startActivityForResult(intent, DirectoryPicker.PICK_DIRECTORY);
				
			}
		});
        

        
        port_group = (RadioGroup) findViewById(R.id.port_radiogroup);
        command_group = (RadioGroup) findViewById(R.id.command_radiogroup);
        
        if(extras != null){
        	width = extras.getInt("width", width);
        	height = extras.getInt("height", height);
			
        	ip_ad1 = extras.getInt("ip_ad1", ip_ad1);
        	ip_ad2 = extras.getInt("ip_ad2", ip_ad2);
        	ip_ad3 = extras.getInt("ip_ad3", ip_ad3);
        	ip_ad4 = extras.getInt("ip_ad4", ip_ad4);
        	ip_port = extras.getInt("ip_port", ip_port);
        	ip_command = extras.getString("ip_command");
        	path=extras.getString("path",path);
    		
			
        	address1_input.setText(String.valueOf(ip_ad1));
        	address2_input.setText(String.valueOf(ip_ad2));
        	address3_input.setText(String.valueOf(ip_ad3));
        	address4_input.setText(String.valueOf(ip_ad4));
        	port_input.setText(String.valueOf(ip_port));
        	command_input.setText(ip_command);
        	pathET.setText(path);
        }

        address1_increment = (Button)findViewById(R.id.address1_increment);
        address1_increment.setOnClickListener(
        		new View.OnClickListener(){
        			public void onClick(View view){       
        				String s = address1_input.getText().toString();
        				int val =ip_ad1;
        				if(!"".equals(s)){
        					val = Integer.parseInt(s);
        				}
        				if(val>=0 && val<255){
        					val += 1;	
        				}else if(val < 0){
        					val = 0;
        				}else if(val >= 255){
        					val = 255;
        				}
        				
        				ip_ad1 = val;
        				address1_input.setText(String.valueOf(ip_ad1), BufferType.NORMAL);

        			}
        		}        		
        );
        address2_increment = (Button)findViewById(R.id.address2_increment);
        address2_increment.setOnClickListener(
        		new View.OnClickListener(){
        			public void onClick(View view){       
        				String s = address2_input.getText().toString();
        				int val =ip_ad2;
        				if(!"".equals(s)){
        					val = Integer.parseInt(s);
        				}
        				if(val>=0 && val<255){
        					val += 1;	
        				}else if(val < 0){
        					val = 0;
        				}else if(val >= 255){
        					val = 255;
        				}
        				
        				ip_ad2 = val;
        				address2_input.setText(String.valueOf(ip_ad2), BufferType.NORMAL);

        			}
        		}        		
        );
        address3_increment = (Button)findViewById(R.id.address3_increment);
        address3_increment.setOnClickListener(
        		new View.OnClickListener(){
        			public void onClick(View view){       
        				String s = address3_input.getText().toString();
        				int val =ip_ad3;
        				if(!"".equals(s)){
        					val = Integer.parseInt(s);
        				}
        				if(val>=0 && val<255){
        					val += 1;	
        				}else if(val < 0){
        					val = 0;
        				}else if(val >= 255){
        					val = 255;
        				}
        				
        				ip_ad3 = val;
        				address3_input.setText(String.valueOf(ip_ad3), BufferType.NORMAL);

        			}
        		}        		
        );
        address4_increment = (Button)findViewById(R.id.address4_increment);
        address4_increment.setOnClickListener(
        		new View.OnClickListener(){
        			public void onClick(View view){       
        				String s = address4_input.getText().toString();
        				int val =ip_ad4;
        				if(!"".equals(s)){
        					val = Integer.parseInt(s);
        				}
        				if(val>=0 && val<255){
        					val += 1;	
        				}else if(val < 0){
        					val = 0;
        				}else if(val >= 255){
        					val = 255;
        				}
        				
        				ip_ad4 = val;
        				address4_input.setText(String.valueOf(ip_ad4), BufferType.NORMAL);

        			}
        		}        		
        );
        
        address1_decrement = (Button)findViewById(R.id.address1_decrement);
        address1_decrement.setOnClickListener(
        		new View.OnClickListener(){
        			public void onClick(View view){       
        				String s = address1_input.getText().toString();
        				int val =ip_ad1;
        				if(!"".equals(s)){
        					val = Integer.parseInt(s);
        				}
        				if(val>0 && val<=255){
        					val -= 1;	
        				}else if(val <= 0){
        					val = 0;
        				}else if(val > 255){
        					val = 255;
        				}
        				
        				ip_ad1 = val;
        				address1_input.setText(String.valueOf(ip_ad1), BufferType.NORMAL);

        			}
        		}        		
        );
        
        address2_decrement = (Button)findViewById(R.id.address2_decrement);
        address2_decrement.setOnClickListener(
        		new View.OnClickListener(){
        			public void onClick(View view){       
        				String s = address2_input.getText().toString();
        				int val =ip_ad2;
        				if(!"".equals(s)){
        					val = Integer.parseInt(s);
        				}
        				if(val>0 && val<=255){
        					val -= 1;	
        				}else if(val <= 0){
        					val = 0;
        				}else if(val > 255){
        					val = 255;
        				}
        				
        				ip_ad2 = val;
        				address2_input.setText(String.valueOf(ip_ad2), BufferType.NORMAL);

        			}
        		}        		
        );
        address3_decrement = (Button)findViewById(R.id.address3_decrement);
        address3_decrement.setOnClickListener(
        		new View.OnClickListener(){
        			public void onClick(View view){       
        				String s = address3_input.getText().toString();
        				int val =ip_ad3;
        				if(!"".equals(s)){
        					val = Integer.parseInt(s);
        				}
        				if(val>0 && val<=255){
        					val -= 1;	
        				}else if(val <= 0){
        					val = 0;
        				}else if(val > 255){
        					val = 255;
        				}
        				
        				ip_ad3 = val;
        				address3_input.setText(String.valueOf(ip_ad3), BufferType.NORMAL);

        			}
        		}        		
        );
        address4_decrement = (Button)findViewById(R.id.address4_decrement);
        address4_decrement.setOnClickListener(
        		new View.OnClickListener(){
        			public void onClick(View view){       
        				String s = address4_input.getText().toString();
        				int val =ip_ad4;
        				if(!"".equals(s)){
        					val = Integer.parseInt(s);
        				}
        				if(val>0 && val<=255){
        					val -= 1;	
        				}else if(val <= 0){
        					val = 0;
        				}else if(val > 255){
        					val = 255;
        				}
        				
        				ip_ad4 = val;
        				address4_input.setText(String.valueOf(ip_ad4), BufferType.NORMAL);

        			}
        		}        		
        );
        
        port_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) { 
                if(checkedId == R.id.port_80){
                	port_input.setText(getString(R.string.port_80));
                }else if(checkedId == R.id.port_8080){
                	port_input.setText(getString(R.string.port_8080));
                }
            }
        });
        
        command_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) { 
                if(checkedId == R.id.command_streaming){
                	command_input.setText(getString(R.string.command_streaming));
                }else if(checkedId == R.id.command_videofeed){
                	command_input.setText(getString(R.string.command_videofeed));
                }
            }
        });
        
        settings_done = (Button)findViewById(R.id.settings_done);
        settings_done.setOnClickListener(
        		new View.OnClickListener(){
        			public void onClick(View view){     
        				
        				String s;
        				
        				s = address1_input.getText().toString();
        				if(!"".equals(s)){
        					ip_ad1 = Integer.parseInt(s);
        				}
        				s = address2_input.getText().toString();
        				if(!"".equals(s)){
        					ip_ad2 = Integer.parseInt(s);
        				}
        				s = address3_input.getText().toString();
        				if(!"".equals(s)){
        					ip_ad3 = Integer.parseInt(s);
        				}
        				s = address4_input.getText().toString();
        				if(!"".equals(s)){
        					ip_ad4 = Integer.parseInt(s);
        				}
        				
        				s = port_input.getText().toString();
        				if(!"".equals(s)){
        					ip_port = Integer.parseInt(s);
        				}
        				
        				s = command_input.getText().toString();
        				ip_command = s;
        				
        				Intent intent = new Intent();
        				//intent.putExtra("width", width);
        				//intent.putExtra("height", height);
        				intent.putExtra("ip_ad1", ip_ad1);
        				intent.putExtra("ip_ad2", ip_ad2);
        				intent.putExtra("ip_ad3", ip_ad3);
        				intent.putExtra("ip_ad4", ip_ad4);
        				intent.putExtra("ip_port", ip_port);
        				intent.putExtra("ip_command", ip_command);
        				intent.putExtra("path", pathET.getText().toString());
        				Log.d("we", "Camera Settings : "+pathET.getText().toString());
        	        
        				setResult(RESULT_OK, intent);
        				finish();
        			}
        		}        		
        	);
	}	
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == DirectoryPicker.PICK_DIRECTORY && resultCode == RESULT_OK) {
    		Bundle extras = data.getExtras();
    		pathET.setText(extras.get(DirectoryPicker.CHOSEN_DIRECTORY).toString());
    		// do stuff with path
    	}
    }
}
