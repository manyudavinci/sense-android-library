package nl.sense_os.service.shared;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class for sensor data producing components.
 * This class gives the ability to have subscribers for the produced data.
 * 
 * Subscribers are notified when there are new samples via the DataProcessor method startNewSample()
 * 
 * Via the DataProcessor method isSampleComplete() the status of the data processors is checked.
 * If all subscribers return true on this function, then the Subscribable will wait until a new sample interval starts.
 * 
 * Sensor data is passed to the subscribers via the DataProcessor method onNewData(SensorData data)
 * 
 * @author Ted Schmidt <ted@sense-os.nl>
 * 
 */
public abstract class Subscribable {

	/** The DataProcessors which are subscribed to this sensor for sensor data*/
	protected Vector<AtomicReference<DataProcessor> > subscribers = new Vector<AtomicReference<DataProcessor> >();
	
	/**
     * Add a sensor data subscriber
     * 
     * This methods adds a DataProcessor as sensor data subscriber for this SenseSensor.
     * If an instance of the DataProcessor is already subscribed then it will be ignored
     * 
     * @param subscriber
     * 		The DataProcessor that wants the sensor data as input
     * @return boolean True if the DataProcessor could subscribe to the service, false if the DataProcessor was already subscribed
     */
	
	public boolean addSubscriber(AtomicReference<DataProcessor>  subscriber) 
	{
		if(!hasSubscriber(subscriber))		
			subscribers.add(subscriber);
		else
			return false;
		return true;		
	}
	
	public Boolean hasSubscriber(AtomicReference<DataProcessor> subscriber)
	{
		for (int i = 0; i < subscribers.size(); i++) 
		{
			AtomicReference<DataProcessor> item = subscribers.elementAt(i);
			if(item.get() == subscriber.get())
				return true;			
		}
		return false;
	}

	/**
	 * Remove Subscriber
	 * 
	 * This method removes a data subscriber if present.
	 * @param subscriber
	 * 		The DataProcessor needs to be un-subscribed
	 */
	
	public void removeSubscriber(AtomicReference<DataProcessor> subscriber) 
	{
		for (int i = 0; i < subscribers.size(); i++) 
		{
			AtomicReference<DataProcessor> item = subscribers.elementAt(i);
			if(item.get() == subscriber.get())
			{
				subscribers.removeElementAt(i);
				--i;
			}
		}
		if(subscribers.contains(subscriber))
			subscribers.remove(subscriber);
	}
	
	/**
	 * Has Subscribers
	 * 
	 * This method returns whether this Subscribable has subscribers
	 * @return Boolean Returns true if there are subscribers
	 */	
	public Boolean hasSubscribers() 
	{
		return !subscribers.isEmpty();			
	}
	
	/**
	 * Send data to DataProcessors
	 * 
	 * This method sends a SensorDataPoint to all the subscribers.
	 * @param dataPoint
	 * 		The SensorDataPoint to send
	 */
	protected void sendToSubscribers(SensorDataPoint dataPoint)
	{
		// TODO: do a-sync
		for (int i = 0; i < subscribers.size(); i++)
		{
			DataProcessor dp =  subscribers.get(i).get();
			if(dp != null && !dp.isSampleComplete())
				dp.onNewData(dataPoint);
		}
			
	}
	
	/**
	 * Notify subscribers of new sample
	 * 
	 * This method calls the startNewSample function of the subscribers to notify that a new sample will be send. 
	 */
	protected void notifySubscribers()
	{
		for (int i = 0; i < subscribers.size(); i++)		
		{
			DataProcessor dp =  subscribers.get(i).get();
			if(dp != null)
				dp.startNewSample();
		}
	}
	
	/**
	 * Check subscribers for complete sample
	 * 
	 * This method calls the isSampleComplete function of the subscribers to see if they need more data.
	 * 
	 * 
	 * @return boolean 
	 * 		Returns true when all the subscribers have complete samples.
	 */
	protected Boolean checkSubscribers()
	{
		Boolean isComplete = true;
		for (int i = 0; i < subscribers.size(); i++)		
		{
			DataProcessor dp =  subscribers.get(i).get();
			if(dp != null)
				isComplete &= dp.isSampleComplete();
		}
		return isComplete;
	}
            
}
