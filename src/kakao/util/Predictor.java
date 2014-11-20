package kakao.util;

/*
 * This interface defining functions of a rating predictor
 * Originally from Zhang Si (zhangsi.cs@gmail.com).
 * See https://github.com/zhangsi/CisRec
 * 
 * @author Kilho Kim
 */

public interface Predictor {
	/*
	 * Train the model of a rating predictor
	 */
	public void trainModel();
	
	/*
	 * Predict the rating value with given user_id and item_id
	 * @param user_id
	 * @param item_id
	 * @param bound whether of bound the predicted value into [minRating, maxRating]
	 * @return
	 */
	public double predict(int userId, int itemId, boolean bound);

}
