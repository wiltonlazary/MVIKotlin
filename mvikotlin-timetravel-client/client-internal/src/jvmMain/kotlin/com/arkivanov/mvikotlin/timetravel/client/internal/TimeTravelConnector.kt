package com.arkivanov.mvikotlin.timetravel.client.internal

import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientStoreFactory.Connector
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelexport.TimeTravelExport
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.ReaderThread
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.WriterThread
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.*
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import java.net.Socket

internal class TimeTravelConnector : Connector {

    override fun connect(host: String, port: Int): Observable<Connector.Event> =
        observable<Connector.Event> { it.connect(host = host, port = port) }
            .onErrorReturn { Connector.Event.Error(it.message) }
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)

    private fun ObservableEmitter<Connector.Event>.connect(host: String, port: Int) {
        val socket = Socket(host, port)

        if (isDisposed) {
            socket.closeSafe()
            return
        }

        val reader =
            ReaderThread<ProtoObject>(
                socket = socket,
                onRead = {
                    onNext(
                        when (it) {
                            is TimeTravelStateUpdate -> Connector.Event.StateUpdate(it)
                            is TimeTravelExport -> Connector.Event.ExportEvents(it.data)
                            else -> {
                                onError(UnsupportedOperationException("Unsupported proto object type: $it"))
                                return@ReaderThread
                            }
                        }
                    )
                },
                onError = ::onError
            )

        val writer = WriterThread(socket = socket, onError = ::onError)

        onNext(Connector.Event.Connected(writer::write))

        reader.start()
        writer.start()

        setDisposable(
            Disposable {
                reader.interrupt()
                writer.interrupt()
                socket.closeSafeAsync()
            }
        )
    }
}
