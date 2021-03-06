package com.observers;

import javax.swing.JPanel;

import bot.event.AbstractEvent;
import utils.DebugEnum;

public class ConsoleExit implements ComObserver {

	private long debugMask;

	public ConsoleExit(long debugMask) {
		this.debugMask = debugMask;
	}

	@Override
	public void onEndIteration(int i, int movimientos, int nume, double alpha, double epsilon, Double R) {
		System.out.println("movimientos: " + movimientos + " nume: " + nume + " episodio " + i);
		System.out.println("alpha: " + alpha + " epsilon: " + epsilon);
	}

	@Override
	public void onEndTrain() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActionTaken() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActionFail() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendMessage(String s) {
		System.out.println(s);
	}

	@Override
	public void onError(String s, boolean fatal) {
		System.err.println(s);
	}

	@Override
	public void onFpsAverageAnnouncement(double fps) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDebugMessage(String s, DebugEnum level) {
		if ((debugMask & (1<<level.ordinal())) != 0)
			System.out.println(s);
	}

	@Override
	public void onEvent(AbstractEvent abstractEvent) {
		System.out.println(abstractEvent.toString());
	}

	@Override
	public void onFullQUpdate(JPanel panel) {
		// TODO Auto-generated method stub
		
	}

}
