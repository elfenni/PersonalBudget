/*
 * Copyright (c) - Software developed by iClaude.
 */

/**
 * Gestione tabella entrate_ripet
 * Funzionamento del database:
 * per ogni aspetto non specificatamente commentato in questa classe fare riferimento alla guida
 * "Siluppare App per Android" - Deitel, da pag. 284.
 * @author Claudio "iClaude" Agostini
 */
package com.flingsoftware.personalbudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import static com.flingsoftware.personalbudget.database.DatabaseOpenHelperWrapper.sDataLock;


public class DBCEntrateRipetute extends DBCExpEarRepeatedAbs {
	
	public DBCEntrateRipetute(Context context) {
		super(context);
	}

	@Override
	public String getTableName() {
		return "entrate_ripet";
	}
	
	/**
	 * Inserisce una entrata ripetuta nella tabella entrate_ripet.
	 * 
	 * @param voce voce della entrata che deve essere ripetuta
	 * @param ripetizione tipo di ripetizione (una_tantum, giornaliero, settimanale, bisettimanale, mensile, 
	 * annuale, giorni_lavorativi, weekend)
	 * @param importo importo entrata
	 * @param valuta simbolo valuta
	 * @param importo_valprin importo nella valuta di default
	 * @param descrizione descrizione della entrata ripetuta
	 * @param data_inizio data di inizio ripetizione entrata nel formato Unix Time, the number of seconds 
	 * since 1970-01-01 00:00:00 UTC
	 * @param flag_fine la ripetizione � gi� finita (1) o � in corso(0)
	 * @param data_fine data di fine ripetizione entrata nel formato Unix Time, the number of seconds 
	 * since 1970-01-01 00:00:00 UTC
	 * @param aggiornato_a aggiornamento tabella entrate_inc fino a questa data Unix Time, the number 
	 * of seconds since 1970-01-01 00:00:00 UTC
	 * @param conto nome del conto
	 * 
	 * @return id id della riga della tabella dove � stato inserito il nuovo record
	 */
	public long inserisciEntrataRipetuta(String voce, String ripetizione, double importo, String valuta, double importo_valprin, String descrizione, long data_inizio, int flag_fine, long data_fine, long aggiornato_a, String conto) {
		ContentValues nuovoContact = new ContentValues();
		nuovoContact.put("voce", voce);
		nuovoContact.put("ripetizione", ripetizione);
		nuovoContact.put("importo", importo);
		nuovoContact.put("valuta", valuta);
		nuovoContact.put("importo_valprin", importo_valprin);
		nuovoContact.put("descrizione", descrizione);
		nuovoContact.put("data_inizio", data_inizio);
		nuovoContact.put("flag_fine", flag_fine);
		nuovoContact.put("data_fine", data_fine);
		nuovoContact.put("aggiornato_a", aggiornato_a);
		nuovoContact.put("conto", conto);
		
		long id;
		synchronized (sDataLock) {
			openModifica();
			id = mioSQLiteDatabase.insert("entrate_ripet", null, nuovoContact);
			close();
		}
		
		return id;
	}


	/**
	 * Ottiene un Cursor che rappresenta una entrata ripetuta dalla tabella entrate_ripet.
	 * 
	 * @param id id della entrata ripetuta di questa tabella
	 * @return un Cursor che rappresenta la entrata ripetuta selezionata
	 */
	@Override
	public Cursor getItemRepeated(long id) {
		return mioSQLiteDatabase.query("entrate_ripet", null, "_id=" + id,  null,  null,  null,  null);
	}


	/**
	 * Restituisce tutte le entrate appartenenti al conto indicato.
	 * @return Cursor con tutti i campi
	 */
	public Cursor getTutteLeEntrateContoX(String conto) {
		return mioSQLiteDatabase.query("entrate_ripet", null, "conto = ?", new String[] {conto}, null, null, null);
	}
	
	
	//metodo di debug: restituisce tutti i record (tutti i campi)
	public Cursor getTutteLeEntrate() {
		return mioSQLiteDatabase.query("entrate_ripet", null, null, null, null, null, null);
	}
}


