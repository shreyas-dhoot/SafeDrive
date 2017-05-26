package com.example.android.myapplication;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Comparator;

public class VenueObject {
	private String name;
	private String city;
	private String venueID;
	private String category;
	private String address;
	private float distance;
	private int likes;
	private int	checkincount;
	private int tipcount;
	private float rating;
	private String stat;
	private String photoJSON;


	public VenueObject() {
		this.name = "";
		this.city = "";
		this.setCategory("");
		this.venueID = "";
		this.distance = 0;
		this.likes = 0;
		this.rating = 0;
		this.checkincount = 0;
		this.tipcount = 0;
		this.address = "";
		this.stat = "";
		this.photoJSON = "";
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress(){
//		if (addressArray.length > 0) {
//			address = addressArray[0];
//			int i;
//			for (i = 1; i < addressArray.length; i++)
//				address = address + ", " + addressArray[i];
//		}
		return address;
	}
	public void setAddress(JSONArray address) throws JSONException {
        int i;
        int length = address.length();
		if(length > 0) {
			this.address = address.getString(0);
			//this.addressArray = new String[length];
			for (i = 1; i < length; i++)
				//this.addressArray[i] = address.getString(i);
				this.address += ", " + address.getString(i);
		}
    }

	public void setAddress(String address){
		this.address = address;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}


	public void setvenueID(String id){ this.venueID = id;}

	public String getvenueID() {return this.venueID;}

	public void setphotoJSON(String json) {
		this.photoJSON = json;
	}

	public String getphotJSON(){
		return this.photoJSON;
	}

	public void setDistance(float distance){
		this.distance = distance;
	}
	public float getDistance(){
		return distance;
	}

	public void setRating(float rating){
		this.rating = rating;
	}
	public float getRating(){
		return rating;
	}

	public void setLikes(int likes){
		this.likes = likes;
	}

	public int getLikes(){
		return this.likes;
	}
	public void setStat(int checkincount, int tipcount){
		this.checkincount = checkincount;
		this.tipcount = tipcount;
		this.stat = Integer.toString(checkincount) + " Been there : " + Integer.toString(tipcount) + " Reviews";
	}


	public void setStat(String stat) {
		this.stat = stat;
	}

	public int getcheckincount(){
		return this.checkincount;
	}

	public int gettipcount(){
		return this.tipcount;
	}

	public String getStat(){
		return stat;
	}

}

class VenueRatingComparator implements Comparator<VenueObject>{

	@Override
	public int compare(VenueObject o1, VenueObject o2) {
		return (int)(o2.getRating() - o1.getRating());
	}
}

class VenueDistanceComparator implements Comparator<VenueObject>{

	@Override
	public int compare(VenueObject o1, VenueObject o2) {
		return (int)(o1.getDistance() - o2.getDistance());
	}
}

