package br.com.dafm.android.buzzzleeper;

import android.app.AlarmManager;
import android.app.Application;
import android.content.Intent;
import android.os.PowerManager;

import com.google.inject.Inject;

public class BuzzzleeperApplication extends Application {
	
	@Inject private PowerManager powerManagerLocation;
	@Inject private PowerManager powerManagerMensagem;
	@Inject private AlarmManager alarmLocationManager;
	@Inject private AlarmManager alarmMensagemManager;
	
	private Intent locationPollerService;
	private Intent mensagemPollerService;
	private PowerManager.WakeLock pollerLocationServiceWakeLock;
	private PowerManager.WakeLock pollerMensagemServiceWakeLock;
}
