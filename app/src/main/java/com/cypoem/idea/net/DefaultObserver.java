package com.cypoem.idea.net;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;
import com.airong.core.BaseImpl;
import com.airong.core.utils.LogUtils;
import com.airong.core.utils.ToastUtils;
import com.cypoem.idea.R;
import com.cypoem.idea.module.BasicResponse;
import com.google.gson.JsonParseException;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import org.json.JSONException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import static com.cypoem.idea.net.DefaultObserver.ExceptionReason.BAD_NETWORK;
import static com.cypoem.idea.net.DefaultObserver.ExceptionReason.CONNECT_ERROR;
import static com.cypoem.idea.net.DefaultObserver.ExceptionReason.CONNECT_TIMEOUT;
import static com.cypoem.idea.net.DefaultObserver.ExceptionReason.PARSE_ERROR;
import static com.cypoem.idea.net.DefaultObserver.ExceptionReason.UNKNOWN_ERROR;

/**
 * Created by zhpan on 2017/4/18.
 *
 */

public abstract class DefaultObserver<T extends BasicResponse> implements Observer<T> {
    private BaseImpl mBAseImpl;
    //  Activity 是否在执行onStop()时取消订阅
    private boolean isAddInStop = false;

    public DefaultObserver(BaseImpl baseImpl) {
        mBAseImpl = baseImpl;
        mBAseImpl.showProgress((Activity) mBAseImpl);
    }


    public DefaultObserver(BaseImpl baseImpl, boolean isShowLoading) {
        mBAseImpl = baseImpl;
        if (isShowLoading) {
            mBAseImpl.showProgress((Activity) mBAseImpl);
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (isAddInStop) {    //  在onStop中取消订阅
            mBAseImpl.addRxStop((Activity) mBAseImpl,d);
        } else { //  在onDestroy中取消订阅
            mBAseImpl.addRxDestroy((Activity)mBAseImpl,d);
        }
    }

    @Override
    public void onNext(T response) {
        mBAseImpl.dismissProgress();
        if (!response.isError()) {
            onSuccess(response);
        } else {
            onFail(response);
        }
        /*if (response.getCode() == 200) {
            onSuccess(response);
        } else {
            onFail(response);
        }*/
    }

    @Override
    public void onError(Throwable e) {
        LogUtils.e("Retrofit", e.getMessage());

        mBAseImpl.dismissProgress();
        if (e instanceof HttpException) {     //   HTTP错误
            onException(BAD_NETWORK);
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {   //   连接错误
            onException(CONNECT_ERROR);
        } else if (e instanceof InterruptedIOException) {   //  连接超时
            onException(CONNECT_TIMEOUT);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {   //  解析错误
            onException(PARSE_ERROR);
        } else {
            onException(UNKNOWN_ERROR);
        }
    }

    @Override
    public void onComplete() {
    }

    /**
     * 请求成功
     *
     * @param response 服务器返回的数据
     */
    abstract public void onSuccess(T response);

    /**
     * 服务器返回数据，但响应码不为200
     *
     * @param response 服务器返回的数据
     */
    public void onFail(T response) {
        String message = response.getMessage();
        if (TextUtils.isEmpty(message)) {
            ToastUtils.show(R.string.response_return_error);
        } else {
            ToastUtils.show(message);
        }
    }

    /**
     * 请求异常
     *
     * @param reason 异常原因
     */
    public void onException(ExceptionReason reason) {
        mBAseImpl.dismissProgress();
        switch (reason) {
            case CONNECT_ERROR:
                ToastUtils.show(R.string.connect_error, Toast.LENGTH_SHORT);
                break;

            case CONNECT_TIMEOUT:
                ToastUtils.show(R.string.connect_timeout, Toast.LENGTH_SHORT);
                break;

            case BAD_NETWORK:
                ToastUtils.show(R.string.bad_network, Toast.LENGTH_SHORT);
                break;

            case PARSE_ERROR:
                ToastUtils.show(R.string.parse_error, Toast.LENGTH_SHORT);
                break;

            case UNKNOWN_ERROR:
            default:
                ToastUtils.show(R.string.unknown_error, Toast.LENGTH_SHORT);
                break;
        }
    }

    /**
     * 请求网络失败原因
     */
    public enum ExceptionReason {
        /**
         * 解析数据失败
         */
        PARSE_ERROR,
        /**
         * 网络问题
         */
        BAD_NETWORK,
        /**
         * 连接错误
         */
        CONNECT_ERROR,
        /**
         * 连接超时
         */
        CONNECT_TIMEOUT,
        /**
         * 未知错误
         */
        UNKNOWN_ERROR,
    }
}
