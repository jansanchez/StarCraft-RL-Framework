package qLearning.agent.qFunction;

import java.io.FileNotFoundException;

import javax.swing.JPanel;

import qLearning.agent.Action;
import qLearning.agent.state.State;

public interface AbstractQFunction {

	double get(State S, Action A);
	void set(State S, Action A, double val);

	void writeToFile(String file) throws FileNotFoundException;
	void readFromFile(String file) throws FileNotFoundException;
	
	JPanel showQ();
}