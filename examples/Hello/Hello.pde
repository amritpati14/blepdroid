import blepdroid.*;
import com.lannbox.rfduinotest.*;
import android.os.Bundle;
import java.util.UUID;

public static UUID UUID_SERVICE = BluetoothHelper.sixteenBitUuid(0x2220);
public static UUID UUID_RECEIVE = BluetoothHelper.sixteenBitUuid(0x2221);
public static UUID UUID_SEND = BluetoothHelper.sixteenBitUuid(0x2222);
public static UUID UUID_DISCONNECT = BluetoothHelper.sixteenBitUuid(0x2223);
public static UUID UUID_CLIENT_CONFIGURATION = BluetoothHelper.sixteenBitUuid(0x2902);

boolean allSetUp = false;


void setup() {
  size(400,400);
  smooth();
}

void draw() {
  background(0);
  fill(255);
}

void mousePressed()
{
  if(allSetUp)
  {
    Blepdroid.getInstance().readCharacteristic(UUID_RECEIVE);
  }
}

public void onCreate(Bundle savedInstanceState) 
{  
    super.onCreate(savedInstanceState);
    getFragmentManager().beginTransaction().add(android.R.id.content, new Blepdroid(this)).commit();
    Blepdroid.getInstance().scanDevices();
}

//void onDeviceDiscovered(String name, String address, UUID id, int rssi, byte[] scanRecord)
void onDeviceDiscovered(BlepdroidDevice device)
{
  println("discovered device " + device.name + " address: " + device.address + " rssi: " + device.rssi );
  if(device.name.equals("RFduino"))
  {
    Blepdroid.getInstance().connectDevice(device.address);
  }
}

void onServicesDiscovered(ArrayList<String> ids, int status)
{
  println(" onServicesDiscovered " + ids );
  println(" 0 means ok, anything else means bad " + status);
  
  HashMap<String, ArrayList<String>> servicesAndCharas = Blepdroid.getInstance().findAllServicesCharacteristics();
    println( servicesAndCharas.size() );
    for( String service : servicesAndCharas.keySet())
    {
      print( service + " has " );
      println( servicesAndCharas.get(service));
    }
    Blepdroid.getInstance().connectToService(UUID_SERVICE); 
    Blepdroid.getInstance().setCharacteristicToListen(UUID_RECEIVE);
    
   allSetUp = true;
}

void onBluetoothRSSI(String device, int rssi)
{
  println(" onBluetoothRSSI " + device + " " + Integer.toString(rssi));
}

void onBluetoothConnection( String device, int state)
{
  println(" onBluetoothConnection " + device + " " + state);
  if(state == 2)
  {
    Blepdroid.getInstance().discoverServices();
  }
}

void onCharacteristicChanged(String characteristic, byte[] data)
{
  println(" onCharacteristicChanged " + characteristic + " " + data);
}

void onDescriptorWrite(String characteristic, String data)
{
  println(" onDescriptorWrite " + characteristic + " " + data);
}

void onDescriptorRead(String characteristic, String data)
{
  println(" onDescriptorRead " + characteristic + " " + data);
}

void onCharacteristicRead(String characteristic, byte[] data)
{
  println(" onCharacteristicRead " + characteristic + " " + data);
}

void onCharacteristicWrite(String characteristic, String data)
{
  println(" onCharacteristicWrite " + characteristic + " " + data);
}