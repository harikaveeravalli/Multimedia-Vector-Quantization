import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.lang.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileOutputStream;
public class MyCompression {
JFrame frame = new JFrame();
JLabel lbIm1;
JLabel lbIm2;
JLabel lbIm3;
int clusters;
int modevalue;
private int nrows;
int datarow=0;
GridBagConstraints c = new GridBagConstraints();
private int ndims;
private int [] label;//generate cluster labels
private int [] withlabel; //if original label exists then load them to the label to compare
private double [][] data; // matrix contains all the records
//ArrayList <ArrayList<Double>> data1 = new ArrayList<ArrayList<Double>>();
int RGBeven[] = new int[144*352*3];
int RGBodd[] = new int[144*352*3]; 
double threshold = 0.001;
int round = 0;
int iterations = 80;
ArrayList<Integer> Grayeven = new ArrayList<Integer>();
ArrayList<Integer> Grayodd = new ArrayList<Integer>();
ArrayList<Integer> Grayeven1 = new ArrayList<Integer>();
ArrayList<Integer> Grayodd1 = new ArrayList<Integer>();
int GrayScaleValues[] =new int[288*352];
int[] evenlines = new int[144*352*3];
int[] oddlines = new int[144*352*3];
int[] evenlines1 = new int[144*352*3];
int[] oddlines1 = new int[144*352*3];
//int GrayScaleValues[]
int RGBvalues[] = new int[288*352];
private double [][]centroids;
public void setImage(BufferedImage img, byte r,int rowval,int colval){
int pix = 0xff000000 | ((r & 0xff) << 16) | ((r & 0xff) << 8) | (r & 0xff);
	img.setRGB(rowval,colval,pix);
}
public void setFrame(){
	frame = new JFrame();
	GridBagLayout g = new GridBagLayout();
	frame.getContentPane().setLayout(g);
}
public void displayleftframe(BufferedImage img) {
	setFrame();
	c.fill = GridBagConstraints.HORIZONTAL;
	c.anchor = GridBagConstraints.CENTER;
	c.gridx = 0;
	c.gridy = 1;
	lbIm1 = new JLabel();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	lbIm1.setIcon(new ImageIcon(img));
	c.fill = GridBagConstraints.HORIZONTAL;
	//c.anchor = GridBagConstraints.CENTER;
	frame.getContentPane().add(lbIm1,c);
	frame.pack();
	frame.setVisible(true);
	lbIm1.repaint();
}
public void displayrightframe(BufferedImage compressedImg){
	//setFrame();
	c.fill = GridBagConstraints.HORIZONTAL;
	c.anchor = GridBagConstraints.CENTER;
	c.gridx = 1;
	c.gridy = 1;
	lbIm2 = new JLabel();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	lbIm2.setIcon(new ImageIcon(compressedImg));
	c.fill = GridBagConstraints.HORIZONTAL;
	//c.anchor = GridBagConstraints.CENTER;
	frame.getContentPane().add(lbIm2,c);
	frame.pack();
	frame.setVisible(true);
	lbIm2.repaint();

}
public void quantizeRawImage(String[] args1){
	try{
	File file = new File(args1[0]);
	InputStream in = new FileInputStream(file);
	long len = file.length();
	int mode = Integer.parseInt(args1[2]);
	byte[] bytes = new byte[(int)len];
	int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead=in.read(bytes, offset, bytes.length-offset)) >= 0) {       
				offset += numRead;
			}	
	BufferedImage img = new BufferedImage(352,288,BufferedImage.TYPE_BYTE_GRAY);
	int ind = 0;
	int count = 0;
	int counteven = 0;
	int countodd = 0;
	if(mode == 1 || mode == 2)
	{
	for(int i=0;i < 288;i++) {
		for(int j=0;j < 352;j++) {
			byte a = 0;
			byte r = bytes[ind];
			//System.out.println("creation of ArrayList");
			if (i%2 == 0) {
				Grayeven.add(r&0xFF);
				//Grayeven[counteven++] = r&0xFF;
			}
			else {
				//Grayodd[countodd++] = r&0xFF;
				Grayodd.add(r&0xFF);
			}
			setImage(img,r,j,i);
			
			ind++;
		}
		}
		if(mode == 1) {
		System.out.println("entered mode1 Quantize Raw Image");
		data = new double[144*352][2];
			for(int i =0;i<144*352;i++) {
				data[i][0] = Grayeven.get(i);
				data[i][1] = Grayodd.get(i);
			}
	}
	System.out.println(Grayeven.size());
	System.out.println(Grayodd.size());
	}
	
	else if(mode == 3){
		int ind1=0;
		for(int i =0;i<288;i++) {
			for(int j=0;j<352;j++) {
				byte a =0;
				byte r =bytes[ind1];
				int value = i%4;
				switch(value)
				{
				case 0:Grayeven.add(r&0xFF);
					   break;
				case 1:Grayodd.add(r&0xFF);
						break;
				case 2:Grayeven1.add(r&0xFF);
						break;
				case 3:Grayodd1.add(r&0xFF);
				       break;
				}
			setImage(img,r,j,i);
			ind1++;
			}
		}
		
		int heightm3 = 288/4;
		int widthm3 = 352/4;
		data = new double[heightm3*widthm3][16];
		datarow = 0;
		int rowval = 0;
		int counter = 0;
		for(int i=0;i<72*352;i+=4) {
			setdataval(data,rowval,i);
			datarow++;
			rowval++;
			counter++;
		//System.out.println(data[rowval][0]+","+data[rowval][1]+","+data[rowval][2]+","+data[rowval][3]+","+data[rowval][4]+","+data[rowval][5]+","+data[rowval][6]+","+data[rowval][7]+","+data[rowval][8]+","+data[rowval][9]+","+data[rowval][10]+","+data[rowval][11]+","+data[rowval][12]+","+data[rowval][13]+","+data[rowval][14]+","+data[rowval][15]);
}
}	
displayleftframe(img);
}
catch(FileNotFoundException e) {
	e.printStackTrace();
}
catch(IOException e){
	e.printStackTrace();
}
}
public void setdataval(double data[][],int rowval,int index){
			data[rowval][0] = Grayeven.get(index);
			data[rowval][1] = Grayeven.get(index+1);
			data[rowval][2] = Grayeven.get(index+2);
			data[rowval][3] = Grayeven.get(index+3);
			data[rowval][4]= Grayodd.get(index);
			data[rowval][5]= Grayodd.get(index+1);
			data[rowval][6]= Grayodd.get(index+2);
			data[rowval][7]= Grayodd.get(index+3);
			data[rowval][8] = Grayeven1.get(index);
			data[rowval][9] = Grayeven1.get(index+1);
			data[rowval][10] = Grayeven1.get(index+2);
			data[rowval][11] = Grayeven1.get(index+3);
			data[rowval][12] = Grayodd1.get(index);
			data[rowval][13] = Grayodd1.get(index+1);
			data[rowval][14] = Grayodd1.get(index+2);
			data[rowval][15] = Grayodd1.get(index+3);
}
private int closestPoint(double[] v) {
	double min_distance = euclideanDist(v,centroids[0]);
	int label1 = 0;
	//System.out.println(clusters);
	for(int i=1;i<clusters;i++) {
		double t = euclideanDist(v,centroids[i]);
		if(min_distance > t) {
			min_distance = t;
			label1 = i;
		}
	}
	return label1;
}
private double euclideanDist(double [] v1, double [] v2) {
	double sum = 0;
	for(int i=0;i<ndims;i++){
		double d = v1[i]-v2[i];
		sum+= d*d;
	}
	return Math.sqrt(sum);
}
private double [][] updateCentroids(int mode) {
	//System.out.println(ndims);
	double[][] newC = new double[clusters][];
	int [] clusterno = new int[clusters];
	int widthval=352;
	int iter=0;
	int iterj=0;
	//Initialization of clusters
	while(iter < clusters){
		clusterno[iter] = 0;
		newC[iter] = new double[ndims];
		for(int j=0;j<ndims;j++) {
			newC[iter][j] = 0;
		}
		iter++;
	}
	//System.out.println("datarow val inside" +datarow);
	if(mode == 1) {
		for (int i=0;i<(144*352);i++) {
		for(int j = 0;j<ndims;j++) {
			newC[label[i]][j] += data[i][j];
		}
		clusterno[label[i]]++;
	}
	}
	else{
	for (int i=0;i<(datarow);i++) {
		for(int j = 0;j<ndims;j++) {
			newC[label[i]][j] += data[i][j];
		}
		clusterno[label[i]]++;
	}
}
//update clusters 
	for(int i = 0;i<clusters;i++) {
		for(int j=0;j<ndims;j++) {
			newC[i][j] = newC[i][j]/clusterno[i];
		}
	}
	return newC;
}
public boolean hasConverged(double [][] c1, double [][] c2, double threshold) {
	double maxv = 0;
	for(int i=0;i<clusters;i++) {
		double d = euclideanDist(c1[i],c2[i]);
		if(maxv < d) 
			maxv = d;
	}
	if(maxv < threshold) 
	return true;
	else
	return false;
}
public int[] getcolor(int colno,double[][] centroids,int rowx,int label[])
			{
				int arr[] = new int[3];
				arr[0] = (int)centroids[label[rowx]][colno]&0xFF;
				arr[1] = (int)centroids[label[rowx]][colno+1]&0xFF;
				arr[2] = (int)centroids[label[rowx]][colno+2]&0xFF;
				return arr;
			}
public void KMeans(String args[], int channel,int ndim) {
	//for(int i = 0 ;i < 288*352;i++) {
	//	System.out.println(GrayScaleValues[i]);
clusters = Integer.parseInt(args[1]);
modevalue = Integer.parseInt(args[2]);
System.out.println("mode value" + modevalue);
System.out.println("clusters value" + clusters);
 //data = new double[288][];
ndims = ndim;
if(channel == 1 && modevalue == 2){
	data = new double[144*352][];
	
	for(int i=0;i<144*352;i+=2)
	{
		double dv[] = new double[4];
	dv[0] = (double)Grayeven.get(i);
	dv[1] = (double)Grayodd.get(i);
	dv[2] = (double)Grayeven.get(i+1);
	dv[3] = (double)Grayodd.get(i+1);
	data[datarow] = dv;
	datarow++;
	//System.out.println(dv[0]+","+dv[1]+","+dv[2]+","+dv[3]);
}
System.out.println("data array length" +datarow);
}
if(channel == 3 && modevalue == 1){
data = new double[144*352][];
for(int i=0;i<nrows;i++) {
	data[i]=new double[6];
}
datarow = 0;
for(int m=0,n=0;m<144*352*3 && n < 144*352*3; m+=3,n+=3) {
	double dv[] = new double[6];
	dv[0] = (double)evenlines[m];
	dv[1] = (double)evenlines[m+1];
	dv[2] = (double)evenlines[m+2];
	dv[3] = (double)oddlines[n];
	dv[4] = (double)oddlines[n+1];
	dv[5] = (double)oddlines[n+2];
	data[datarow] = dv;
	//System.out.println(data[datarow][0]+","+data[datarow][1]+","+data[datarow][2]+","+data[datarow][3]+","+data[datarow][4]+","+data[datarow][5]);
	datarow++;
}
}
if(channel == 3 && modevalue == 2) {
	int datasize = (288*352)/4;
	data = new double[datasize][];
	datarow = 0;
	int dvcount = 0;
	for(int m=0,n=0;m<144*352*3 && n < 144*352*3; m+=6,n+=6){
		double dv[] = new double[12];
		dv[dvcount] = (double)evenlines[m];
		dv[dvcount+1] = (double)evenlines[m+1];
		dv[dvcount+2] = (double)evenlines[m+2];
		dv[dvcount+3] = (double)oddlines[n];
		dv[dvcount+4] = (double)oddlines[n+1];
		dv[dvcount+5] = (double)oddlines[n+2];
		dv[dvcount+6] = (double)evenlines[m+3];
		dv[dvcount+7] = (double)evenlines[m+4];
		dv[dvcount+8] = (double)evenlines[m+5];
		dv[dvcount+9] = (double)oddlines[n+3];
		dv[dvcount+10] = (double)oddlines[n+4];
		dv[dvcount+11] = (double)oddlines[n+5];
		data[datarow] = dv;
		//System.out.println(dv[0]+","+dv[1]+","+dv[2]+","+dv[3]+","+dv[4]+","+dv[5]+","+dv[6]+","+dv[7]+","+dv[8]+","+dv[9]+","+dv[10]+","+dv[11]);
		datarow++;

	}
}
if(channel == 3 && modevalue == 3){
	int datasize = (288*352)/16;
	data = new double[datasize][];
	datarow = 0;
	for(int m=0,n=0;m<76*352*3 && n<76*352*3;m+=12,n+=12){
		double dv[] = new double[48];
		if(datarow < 6336){
		dv[0] = (double)evenlines[m];
		dv[1] = (double)evenlines[m+1];
		dv[2] = (double)evenlines[m+2];
		dv[3] = (double)evenlines[m+3];
		dv[4] = (double)evenlines[m+4];
		dv[5] = (double)evenlines[m+5];
		dv[6] = (double)evenlines[m+6];
		dv[7] = (double)evenlines[m+7];
		dv[8] = (double)evenlines[m+8];
		dv[9] = (double)evenlines[m+9];
		dv[10] = (double)evenlines[m+10];
		dv[11] = (double)evenlines[m+11];
		dv[12] = (double)oddlines[n];
		dv[13] = (double)oddlines[n+1];
		dv[14] = (double)oddlines[n+2];
		dv[15] = (double)oddlines[n+3];
		dv[16] = (double)oddlines[n+4];
		dv[17] = (double)oddlines[n+5];
		dv[18] = (double)oddlines[n+6];
		dv[19] = (double)oddlines[n+7];
		dv[20] = (double)oddlines[n+8];
		dv[21] = (double)oddlines[n+9];
		dv[22] = (double)oddlines[n+10];
		dv[23] = (double)oddlines[n+11];
		dv[24] = (double)evenlines1[m];
		dv[25] = (double)evenlines1[m+1];
		dv[26] = (double)evenlines1[m+2];
		dv[27] = (double)evenlines1[m+3];
		dv[28] = (double)evenlines1[m+4];
		dv[29] = (double)evenlines1[m+5];
		dv[30] = (double)evenlines1[m+6];
		dv[31] = (double)evenlines1[m+7];
		dv[32] = (double)evenlines1[m+8];
		dv[33] = (double)evenlines1[m+9];
		dv[34] = (double)evenlines1[m+10];
		dv[35] = (double)evenlines1[m+11];
		dv[36] = (double)oddlines1[n];
		dv[37] = (double)oddlines1[n+1];
		dv[38] = (double)oddlines1[n+2];
		dv[39] = (double)oddlines1[n+3];
		dv[40] = (double)oddlines1[n+4];
		dv[41] = (double)oddlines1[n+5];
		dv[42] = (double)oddlines1[n+6];
		dv[43] = (double)oddlines1[n+7];
		dv[44] = (double)oddlines1[n+8];
		dv[45] = (double)oddlines1[n+9];
		dv[46] = (double)oddlines1[n+10];
		dv[47] = (double)oddlines1[n+11];
		data[datarow] = dv;
		datarow++;	
		//System.out.println(dv[0]+","+dv[1]+","+dv[2]+","+dv[3]+","+dv[4]+","+dv[5]+","+dv[6]+","+dv[7]+","+dv[8]+","+dv[9]+","+dv[10]+","+dv[11]);
	}
}
}
//centroids
centroids = new double[clusters][];
ArrayList<Integer> duplicateid = new ArrayList<Integer>();
int changewidth;
System.out.println("dimensions" + ndims);
for(int i=0;i<clusters;i++) {
	int c;
	int randval;
	int nval = (144*352);
	if(modevalue == 1)
	{
		randval = nval;
	}
	else
	{
		randval = datarow;
	}
	do{
		c = (int) (Math.random()*randval);

	}while(duplicateid.contains(c));
	duplicateid.add(c);
	centroids[i] = new double[ndims];

	for(int j=0;j<ndims;j++) {
		centroids[i][j] = data[c][j];
		//System.out.println(centroids[i][j]);
	}
}
//Running Kmeans Algorithms 
double[][] c1 = centroids;
int heightval = 144;
int widthval = 352;
while(true) {
	centroids = c1;
	if(modevalue == 1){
	label = new int[144*352];
	for (int i=0;i<(heightval*widthval);i++) 
		label[i] = closestPoint(data[i]);
	c1 = updateCentroids(modevalue);
	round++;
	if(iterations > 0 && round >=iterations || hasConverged(centroids,c1,threshold))
		break;
}
if(modevalue == 2 || modevalue == 3) {
	
	label = new int[datarow];

	for (int i=0;i<(datarow);i++) {
		label[i] = closestPoint(data[i]);
		//System.out.println(data[i]);
	}
	c1 = updateCentroids(modevalue);
	round++;

	if(iterations > 0 && round >=iterations || hasConverged(centroids,c1,threshold))
		break;
}
}
byte[] bytes1 = new byte[288*352*channel];
int index = 0;
BufferedImage quantizedImage;
//quantizedImage = new BufferedImage(352,288,BufferedImage.TYPE_BYTE_GRAY);
if(channel == 1) {
	System.out.println("entered the kmeans Algorithms");
	quantizedImage = new BufferedImage(352,288,BufferedImage.TYPE_BYTE_GRAY);
	//System.out.println(nrows);
	if(modevalue == 1){
	for(int x = 0; x<(144*352); x++) {
		if(x%352 == 0 && x != 0){
			index = index + 352;	
		}
	bytes1[index] = (byte)((int)centroids[label[x]][0] & 0xFF);
	//System.out.println(bytes[index]);
	bytes1[index+352] = (byte)((int)centroids[label[x]][1] & 0xFF);
	index++;
}
}
else if(modevalue == 2) {
int index1 =0;
int _width = 352;
	for(int x = 0; x<(datarow); x++) {
		if(index1%352 == 0){
			index1 = index1 + _width;
			//System.out.println("entered loop" +index);
		}
	if(index1 < 101023)
	{
	bytes1[index1] = (byte)(((int)centroids[label[x]][0]) & 0xFF);
	//System.out.println(bytes[index]);
	int indval = (index1+352);
	bytes1[indval] = (byte)(((int)centroids[label[x]][1]) & 0xFF);
	bytes1[++index1] = (byte)(((int)centroids[label[x]][2]) & 0xFF);
	int indval2 = (index1+352);
	bytes1[indval2] = (byte)(((int)centroids[label[x]][3]) & 0xFF);

	//bytes1[index-1+352] = (byte)((int)centroids[label[x]][3] & 0xFF);
	//System.out.println(centroids[label[x]][0]+","+centroids[label[x]][1]+","+centroids[label[x]][2]+","+centroids[label[x]][3]+"\n");
//System.out.println(index1);	
index1++;
}
}
System.out.println(datarow);
}
else if(modevalue == 3) {
	System.out.println(datarow);
	int index2=0;
	int oddval = 0;
	for(int x = 0; x<datarow;x++) {
		if(index2%352 == 0) {
			oddval = 1;
			index2 = index2 + 352*3;
		}

		if(index2 < 100304) {
			bytes1[index2] = (byte)(((int)centroids[label[x]][0]) & 0xFF);
			//System.out.println(bytes1[index2]);
			bytes1[(index2)+352] = (byte)(((int)centroids[label[x]][4]) & 0xFF);
			bytes1[index2 + 352*2] = (byte)(((int)centroids[label[x]][8]) & 0xFF);
			bytes1[index2+352*3] = (byte)(((int)centroids[label[x]][12]) & 0xFF);
			bytes1[++index2] = (byte)(((int)centroids[label[x]][1]) & 0xFF);
			bytes1[(index2)+352] = (byte)(((int)centroids[label[x]][5]) & 0xFF);
			bytes1[index2+ 352*2] = (byte)(((int)centroids[label[x]][9]) & 0xFF);
			bytes1[index2+ 352*3] = (byte)(((int)centroids[label[x]][13]) & 0xFF);
			bytes1[++index2] = (byte)(((int)centroids[label[x]][2]) & 0xFF);
			bytes1[(index2)+352] = (byte)(((int)centroids[label[x]][6]) & 0xFF);
			bytes1[index2 + 352*2] = (byte)(((int)centroids[label[x]][10]) & 0xFF);
			bytes1[index2+ 352*3] = (byte)(((int)centroids[label[x]][14]) & 0xFF);
			bytes1[++index2] = (byte)(((int)centroids[label[x]][3]) & 0xFF);
			bytes1[(index2)+ 352] = (byte)(((int)centroids[label[x]][7]) & 0xFF);
			bytes1[index2 + 352*2] = (byte)(((int)centroids[label[x]][11]) & 0xFF);
			bytes1[index2+352*3] = (byte)(((int)centroids[label[x]][15]) & 0xFF);
		index2++;
		}
	}
}
System.out.println("printing Gray Image");
	int ind = 0;
	for(int y=0;y<288;y++) {
	for(int x=0;x<352;x++) {
		byte a = 0;
		byte r1 = bytes1[ind];
		setImage(quantizedImage,r1,x,y);
		ind++;
	}
}
}
else {
	System.out.println("entered the RGB block in Kmeans");
	quantizedImage = new BufferedImage(352,288,BufferedImage.TYPE_INT_RGB);
	int row_num1 = 0;
	if(channel == 3 && modevalue == 1){
	for(int i=0;i<288;i=i+2){
		for(int j=0;j<352;j++) {
			int red = (int) centroids[label[row_num1]][0]&0xFF;
			int green = (int) centroids[label[row_num1]][1]&0xFF;
			int blue = (int) centroids[label[row_num1]][2]&0xFF;
			Color t1 = new Color(red,green,blue);
			quantizedImage.setRGB(j,i,t1.getRGB());
			row_num1++;
		}
	}
	int row_num = 0;
	for(int m = 1;m<288; m=m+2){
		for(int n=0;n<352;n++) {
			int red1 = (int) centroids[label[row_num]][3]&0xFF;
			int green1 =(int) centroids[label[row_num]][4]&0xFF;
			int blue1 =(int) centroids[label[row_num]][5]&0xFF;
			Color t2 = new Color(red1,green1,blue1);
			quantizedImage.setRGB(n,m,t2.getRGB());
			row_num++;
		}
	}
	System.out.println("Completed RGB block in KMeans");
}
else if(channel == 3 && modevalue == 2){
	int row_num2 = 0;

	//quantizedImage = new BufferedImage(352,288,BufferedImage.TYPE_INT_RGB);
	for(int i=0;i<288;i=i+2){
		for(int j=0;j<352;j+=2) {
			if(row_num2 < 25344){
			int red = (int) centroids[label[row_num2]][0]&0xFF;
			int green = (int) centroids[label[row_num2]][1]&0xFF;
			int blue = (int) centroids[label[row_num2]][2]&0xFF;
			Color t1 = new Color(red,green,blue);
			int red1 = (int) centroids[label[row_num2]][6]&0xFF;
			int green1 = (int) centroids[label[row_num2]][7]&0xFF;
			int blue1 = (int) centroids[label[row_num2]][8]&0xFF;
			Color t2 = new Color(red,green,blue);
			quantizedImage.setRGB(j,i,t1.getRGB());
			quantizedImage.setRGB(j+1,i+1,t2.getRGB());
			row_num2++;
		}
	}
	}
	int rowx =0 ;
	for(int m = 1;m<287; m=m+2){
		for(int n=0;n<352;n+=2) {
			if(rowx < 25344){
			int red1 = (int) centroids[label[rowx]][3]&0xFF;
			int green1 =(int) centroids[label[rowx]][4]&0xFF;
			int blue1 =(int) centroids[label[rowx]][5]&0xFF;
			Color t2 = new Color(red1,green1,blue1);
			int red2 = (int) centroids[label[rowx]][9]&0xFF;
			int green2 =(int) centroids[label[rowx]][10]&0xFF;
			int blue2 =(int) centroids[label[rowx]][11]&0xFF;
			Color t3 = new Color(red2,green2,blue2);
			quantizedImage.setRGB(n,m,t2.getRGB());
			quantizedImage.setRGB(n+1,m+1,t3.getRGB());
			rowx++;
		}
	}
}
}
else if(channel == 3 && modevalue == 3) {
	int row_num3=0;
	int endline =(288*352)/16;
	
	for(int i=0;i<288;i=i+4){
		for(int j=0;j<352;j+=4) {
			if(row_num3 < endline){
			
			int arr[] = getcolor(0,centroids,row_num3,label);
			Color t1 = new Color(arr[0],arr[1],arr[2]);
			int arr2[] = getcolor(3,centroids,row_num3,label);
			Color t2 = new Color(arr2[0],arr2[1],arr2[2]);
			int arr3[] = getcolor(6,centroids,row_num3,label);
			Color t3 = new Color(arr3[0],arr3[1],arr3[2]);
			int arr4[] = getcolor(9,centroids,row_num3,label);
			Color t4 = new Color(arr4[0],arr4[1],arr4[2]);
			int arr5[] = getcolor(24,centroids,row_num3,label);
			Color t5 = new Color(arr5[0],arr5[1],arr5[2]);
			int arr6[] = getcolor(27,centroids,row_num3,label);
			Color t6 = new Color(arr6[0],arr6[1],arr6[2]);
			int arr7[] = getcolor(30,centroids,row_num3,label);
			Color t7 = new Color(arr7[0],arr7[1],arr7[2]);
			int arr8[] = getcolor(33,centroids,row_num3,label);
			Color t8 = new Color(arr8[0],arr8[1],arr8[2]);
			quantizedImage.setRGB(j,i,t1.getRGB());
			quantizedImage.setRGB(j+1,i,t2.getRGB());
			quantizedImage.setRGB(j+2,i,t3.getRGB());
			quantizedImage.setRGB(j+3,i,t4.getRGB());
			quantizedImage.setRGB(j,i+2,t5.getRGB());
			quantizedImage.setRGB(j+1,i+2,t6.getRGB());
			quantizedImage.setRGB(j+2,i+2,t7.getRGB());
			quantizedImage.setRGB(j+3,i+2,t8.getRGB());
			
			
			row_num3++;
		}
	}
	}
	row_num3 =0 ;
	for(int m = 1;m<287; m=m+4){
		for(int n=0;n<352;n+=4) {
			if(row_num3 < endline){
				int arr[] = getcolor(12,centroids,row_num3,label);
			Color t1 = new Color(arr[0],arr[1],arr[2]);
			int arr2[] = getcolor(15,centroids,row_num3,label);
			Color t2 = new Color(arr2[0],arr2[1],arr2[2]);
			int arr3[] = getcolor(18,centroids,row_num3,label);
			Color t3 = new Color(arr3[0],arr3[1],arr3[2]);
			int arr4[] = getcolor(21,centroids,row_num3,label);
			Color t4 = new Color(arr4[0],arr4[1],arr4[2]);
			int arr5[] = getcolor(36,centroids,row_num3,label);
			Color t5 = new Color(arr5[0],arr5[1],arr5[2]);
			int arr6[] = getcolor(39,centroids,row_num3,label);
			Color t6 = new Color(arr6[0],arr6[1],arr6[2]);
			int arr7[] = getcolor(42,centroids,row_num3,label);
			Color t7 = new Color(arr7[0],arr7[1],arr7[2]);
			int arr8[] = getcolor(45,centroids,row_num3,label);
			Color t8 = new Color(arr8[0],arr8[1],arr8[2]);
			quantizedImage.setRGB(n,m,t1.getRGB());
			quantizedImage.setRGB(n+1,m,t2.getRGB());
			quantizedImage.setRGB(n+2,m,t3.getRGB());
			quantizedImage.setRGB(n+3,m,t4.getRGB());
			quantizedImage.setRGB(n,m+2,t5.getRGB());
			quantizedImage.setRGB(n+1,m+2,t6.getRGB());
			quantizedImage.setRGB(n+2,m+2,t7.getRGB());
			quantizedImage.setRGB(n+3,m+2,t8.getRGB());
			
			row_num3++;
		
	}
}
}
}
}
displayrightframe(quantizedImage);
}
public void quantizeRGBImage(String[] args1){
	int height = 288;
	int width = 352;
	int index_even = 0;
	int index_odd = 0;
	int mode = Integer.parseInt(args1[2]);
	try{
	File file = new File(args1[0]);
	InputStream in = new FileInputStream(file);
	long len = file.length();
	byte[] bytes1 = new byte[(int)len];
	System.out.println("The image is of this many bytes:"+ bytes1.length); 
	int offset = 0;
			int numRead = 0;
			while (offset < bytes1.length && (numRead=in.read(bytes1, offset, bytes1.length-offset)) >= 0) {       
				offset += numRead;	
			}	
	BufferedImage img = new BufferedImage(352,288,BufferedImage.TYPE_INT_RGB);
	int ind = 0;
	int rgbcounter = 0;
	int ai=0;
	int bi=0;
	if(mode == 1 || mode == 2){
	System.out.println("entering the for loop");
	for(int i = 0; i < height; i++) {
		for(int j=0;j< width; j++) {
			byte a=0;
			byte r = bytes1[ind];
			byte g = bytes1[ind+height*width];
			byte b = bytes1[ind+height*width*2];
			int r_int = r&0xFF;
			int g_int = g&0xFF;
			int b_int = b&0xFF;
			
			int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
			img.setRGB(j,i,pix);
			ind++;
			if(i%2 == 0)
			{
				evenlines[ai++] = r_int;
				evenlines[ai++] = g_int;
				evenlines[ai++] = b_int;//ai++;
			}
			else
			{
				oddlines[bi++] = r_int;
				oddlines[bi++] = g_int;
				oddlines[bi++] = b_int;	//bi++;
			}	
		}
	}
	}
	else if(mode == 3){
		int aii=0;
		int bii=0;
		int ci=0;
		int di=0;
		for(int i = 0; i < height; i++) {
		for(int j=0;j< width; j++) {
			byte a=0;
			byte r = bytes1[ind];
			byte g = bytes1[ind+height*width];
			byte b = bytes1[ind+height*width*2];
			int r_int = r&0xFF;
			int g_int = g&0xFF;
			int b_int = b&0xFF;
			
			int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
			img.setRGB(j,i,pix);
			ind++;
			switch(i%4){
				case 0:  
				{
				evenlines[aii++] = r_int;
				evenlines[aii++] = g_int;
				evenlines[aii++] = b_int;
				break;
			}
				case 1:  
				{
				oddlines[bii++] = r_int;
				oddlines[bii++] = g_int;
				oddlines[bii++] = b_int;
				break;
			}
				case 2:
				{
				evenlines1[ci++] =r_int;
				evenlines1[ci++] = g_int;
				evenlines1[ci++] = b_int;
				break;
			}
				case 3:
				{
				oddlines1[di++] = r_int;
				oddlines1[di++] = g_int;
				oddlines1[di++] = b_int;
				break;
			}
			}	
	}
	}
}
	System.out.println("entered the RGB function");
	displayleftframe(img);
}
catch(FileNotFoundException e) {
	e.printStackTrace();
}
catch(IOException e){
	e.printStackTrace();
}
}
public static void main(String[] args) {
MyCompression obj = new MyCompression();
String line = args[0];
String pattern ="(.)(rgb)";
Pattern r = Pattern.compile(pattern);
Matcher m = r.matcher(line);
int modeval = Integer.parseInt(args[2]);
if(m.find()){
System.out.println("Compressing RGB image");
obj.quantizeRGBImage(args);
if(modeval == 1){
obj.KMeans(args,3,6);
}
else if(modeval == 2){
	obj.KMeans(args,3,12);
}
else
{
	obj.KMeans(args,3,48);
}
}
else 
{
	System.out.println("Quantizing Raw Image");
	obj.quantizeRawImage(args);
	if(modeval == 1)
	{
		obj.KMeans(args,1,2);
	}
	else if(modeval == 2)
	{
		obj.KMeans(args,1,4);
	}
	else 
	{
		obj.KMeans(args,1,16); 
	}	
}
}
}
