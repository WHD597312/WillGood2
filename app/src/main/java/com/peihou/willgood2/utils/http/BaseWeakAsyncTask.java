package com.peihou.willgood2.utils.http;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public abstract class BaseWeakAsyncTask<Params,Progress,Result,WeakTarget> extends AsyncTask<Params,Progress,Result> {

    //外部类弱引用
    protected  WeakReference<WeakTarget> mTarget;

    public BaseWeakAsyncTask(WeakTarget target){
        mTarget = new WeakReference<WeakTarget>(target);
    }


    @Override
    protected Result doInBackground(Params... params) {

        final WeakTarget target = mTarget.get();
        if(target != null){
            return this.doInBackground(target,params);
        }else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        final WeakTarget target = mTarget.get();
        if(target != null){
            this.onPostExecute(target,result);
        }
    }


    protected abstract Result doInBackground(WeakTarget target,Params... params);
    protected abstract void onPostExecute(WeakTarget target,Result result);
}
