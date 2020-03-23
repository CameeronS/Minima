package org.minima.database.mmr;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.minima.objects.Coin;
import org.minima.objects.Proof;
import org.minima.objects.Proof.ProofChunk;
import org.minima.objects.base.MiniByte;
import org.minima.objects.base.MiniData;
import org.minima.objects.base.MiniHash;
import org.minima.objects.base.MiniNumber;
import org.minima.utils.Crypto;
import org.minima.utils.Streamable;
import org.minima.utils.json.JSONArray;
import org.minima.utils.json.JSONObject;

public class MMRProof extends Proof {
	
	/**
	 * The block time this proof points to
	 */
	MiniNumber mBlockTime = MiniNumber.ZERO;
	
	/**
	 * The Entry number in the MMR
	 */
	MiniNumber mEntryNumber = MiniNumber.ZERO;
	
	/**
	 * The Provable data
	 */
	MMRData mData;
	
	public MMRProof() {
		super();
	}
		
	public MMRProof(MiniNumber zEntryNumber, MMRData zInitialData, MiniNumber zBlockTime) {
		mEntryNumber = zEntryNumber;
		mData        = zInitialData;
		mBlockTime   = zBlockTime;
		
		setData(mData.getFinalHash());
	}
	
	public MiniNumber getBlockTime() {
		return mBlockTime;
	}
	
	public MiniNumber getEntryNumber() {
		return mEntryNumber;
	}
	
	public MMRData getMMRData() {
		return mData;
	}
	
	/**
	 * Check this proof is the same as this coin..
	 * 
	 * @param zCoin
	 * @return
	 */
	public boolean checkCoin(Coin zCoin) {
		//Check Against
		Coin cc = getMMRData().getCoin();
		
		//Is this input for the correct details..
		boolean coinidcheck  = cc.getCoinID().isExactlyEqual(zCoin.getCoinID());
		boolean amountcheck  = cc.getAmount().isEqual(zCoin.getAmount());
		boolean addresscheck = cc.getAddress().isExactlyEqual(zCoin.getAddress());
		boolean tokencheck   = cc.getTokenID().isExactlyEqual(zCoin.getTokenID());
		
		return coinidcheck && amountcheck && addresscheck && tokencheck;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject(); 
		
		obj.put("blocktime", mBlockTime.toString());
		obj.put("entry", mEntryNumber.toString());
		obj.put("data", mData.toJSON());
		obj.put("proof",super.toJSON());
		
		return obj;
	}
	
//	public JSONObject toProofChainJSONOnly() {
//		JSONObject json = new JSONObject();
//		
//		json.put("data", mData);
//		json.put("chainsha", getChainSHAProof().to0xString());
//		
//		return json;
//	}
	
	@Override
	public String toString() {
		return toJSON().toString();
	}

	@Override
	public void writeDataStream(DataOutputStream zOut) throws IOException {
		mBlockTime.writeDataStream(zOut);
		mEntryNumber.writeDataStream(zOut);
		mData.writeDataStream(zOut);
		
		super.writeDataStream(zOut);
	}

	@Override
	public void readDataStream(DataInputStream zIn) throws IOException {
		mBlockTime   = MiniNumber.ReadFromStream(zIn);
		mEntryNumber = MiniNumber.ReadFromStream(zIn);
		mData        = MMRData.ReadFromStream(zIn);
		
		super.readDataStream(zIn);
	}
	
	public static MMRProof ReadFromStream(DataInputStream zIn){
		MMRProof proof = new MMRProof();
		
		try {
			proof.readDataStream(zIn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return proof;
	}
}
