package simulation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;

import org.jfree.data.xy.XYSeries;

import sim.display.Controller;
import sim.util.media.chart.TimeSeriesChartGenerator;

public class StatsManager implements PropertyChangeListener {
	ArrayList<String> types;
	HashMap<String,Double> populations;
	HashMap<String,Integer>	bornThisStep;
	HashMap<String,Integer> diedThisStep;
	HashMap<String,Float> weightEatenThisStep;
	
	HashMap<String,XYSeries> populationsSeries;
	HashMap<String,XYSeries> bornThisStepSeries;
	HashMap<String,XYSeries> diedThisStepSeries;
	HashMap<String,XYSeries> weightEatenThisStepSeries;
	
	
	private TimeSeriesChartGenerator chartPop = new sim.util.media.chart.TimeSeriesChartGenerator();
	private TimeSeriesChartGenerator chartBorn = new sim.util.media.chart.TimeSeriesChartGenerator();
	private TimeSeriesChartGenerator chartDied = new sim.util.media.chart.TimeSeriesChartGenerator();
	private TimeSeriesChartGenerator chartAte = new sim.util.media.chart.TimeSeriesChartGenerator();
	
	
	
	
	
	public StatsManager(){
		types = new ArrayList<String>();
		populations=new HashMap<String,Double>();
		bornThisStep=new HashMap<String,Integer>();
		diedThisStep=new HashMap<String,Integer>();
		weightEatenThisStep=new HashMap<String,Float>();
		populationsSeries=new HashMap<String,XYSeries>();
		bornThisStepSeries=new HashMap<String,XYSeries>();
		diedThisStepSeries=new HashMap<String,XYSeries>();
		weightEatenThisStepSeries=new HashMap<String,XYSeries>();
		chartPop.setTitle("Species populations");
		chartPop.setRangeAxisLabel("Individuals");
		chartPop.setDomainAxisLabel("Steps");
		
		chartBorn.setTitle("Species birthrates");
		chartBorn.setRangeAxisLabel("Individuals/Individuals");
		chartBorn.setDomainAxisLabel("Steps");
		
		chartDied.setTitle("Species deathrates");
		chartDied.setRangeAxisLabel("Individuals/Individuals");
		chartDied.setDomainAxisLabel("Steps");
		
		chartAte.setTitle("Species feeding rates");
		chartAte.setRangeAxisLabel("Kg/Individuals");
		chartAte.setDomainAxisLabel("Steps");
		
	}
	
	public void addStatForSpecies(String speciesType){
		//keep type
		types.add(speciesType);
		
		//keep datas
		populations.put(speciesType, (double)0);
		bornThisStep.put(speciesType, 0);
		diedThisStep.put(speciesType, 0);
		weightEatenThisStep.put(speciesType, 0f);	
		
		//kep series
		populationsSeries.put(speciesType, new XYSeries((speciesType+" population"),false));
		bornThisStepSeries.put(speciesType, new XYSeries((speciesType+" birthrate"),false));
		diedThisStepSeries.put(speciesType, new XYSeries((speciesType+" deathrate"),false));
		weightEatenThisStepSeries.put(speciesType, new XYSeries((speciesType+" feeding rate"),false));
		
		//setCharts
		chartPop.addSeries(populationsSeries.get(speciesType), null);
		chartBorn.addSeries(bornThisStepSeries.get(speciesType), null);
		chartDied.addSeries(diedThisStepSeries.get(speciesType), null);
		chartAte.addSeries(weightEatenThisStepSeries.get(speciesType), null);
		
	}
	
	public void updateCharts(double x){
		Iterator<String> it = types.iterator();
		XYSeries currentSerie;
		while(it.hasNext()){
			String sp = it.next();
			
			//population
			double currentPop = populations.get(sp);
			currentSerie = populationsSeries.get(sp);
			currentSerie.add(x, populations.get(sp)/1f, true);
			populationsSeries.put(sp, currentSerie);
			
			//born
			currentSerie = bornThisStepSeries.get(sp);
			float newVal;
			if(currentPop!=0){
				newVal = bornThisStep.get(sp)/((float)currentPop);
				System.out.println("new born "+newVal);
			}else{
				newVal=0;
			}
			 
			currentSerie.add(x,newVal, true);
			bornThisStepSeries.put(sp, currentSerie);
			bornThisStep.put(sp, 0);
			
			//died
			currentSerie = diedThisStepSeries.get(sp);
			if(currentPop!=0){
				newVal = diedThisStep.get(sp)/((float)currentPop);
				System.out.println("new died "+newVal);
			}else{
				newVal=0;
			}
			
			currentSerie.add(x, newVal, true);
			diedThisStepSeries.put(sp, currentSerie);
			diedThisStep.put(sp, 0);
			
			//ate
			currentSerie = weightEatenThisStepSeries.get(sp);
			if(currentPop!=0){
				newVal = weightEatenThisStep.get(sp)/((float)currentPop);
				System.out.println("new ate "+newVal);
			}else{
				newVal=0;
			}
			
			currentSerie.add(x, newVal, true);
			weightEatenThisStepSeries.put(sp, currentSerie);
			weightEatenThisStep.put(sp, 0f);		
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if(arg0.getPropertyName().equals("was_born")){
			System.out.println("bornthisStep is :"+bornThisStep);
			System.out.println("type is : "+arg0.getOldValue());
			
			bornThisStep.put((String) arg0.getOldValue(),bornThisStep.get(arg0.getOldValue())+1);
			populations.put((String) arg0.getOldValue(),populations.get(arg0.getOldValue())+1);
		} else if(arg0.getPropertyName().equals("died")){
			diedThisStep.put((String) arg0.getOldValue(),diedThisStep.get(arg0.getOldValue())+1);
			populations.put((String) arg0.getOldValue(),populations.get(arg0.getOldValue())-1);
		} else if(arg0.getPropertyName().equals("ate")){
			System.out.println("ate "+((Float)arg0.getNewValue())+" received !");
			weightEatenThisStep.put((String) arg0.getOldValue(),weightEatenThisStep.get(arg0.getOldValue())+((Float)arg0.getNewValue()));
		}
		
	}

	public void createFrames(Controller controller) {
		
		//System.out.println("createFrames called !");
		
		JFrame currentFrame = chartPop.createFrame();
		currentFrame.show();
		currentFrame.pack();
		controller.registerFrame(currentFrame);
		
		currentFrame = chartBorn.createFrame();
		currentFrame.show();
		currentFrame.pack();
		controller.registerFrame(currentFrame);
		
		
		currentFrame = chartDied.createFrame();
		currentFrame.show();
		currentFrame.pack();
		controller.registerFrame(currentFrame);
		
		
		currentFrame = chartAte.createFrame();
		currentFrame.show();
		currentFrame.pack();
		controller.registerFrame(currentFrame);
		
	}

	
}

/*
series = new XYSeries("Ipop",false);
chart.addSeries(series, null);
*/


//TODO population
//TODO natalité
//TODO mortalité
//TODO quantité mangé