/*
 * Copyright 2020 Francesco Menzani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.competitors;

import test.AbstractReader;
import test.Profiler;
import test.TestThreadGroup;
import test.object.Event;

import java.util.Queue;

class Reader extends TestThread implements AbstractReader {
    static long runGroupAsync(Queue<Event> ringBuffer, Profiler profiler) {
        TestThreadGroup group = new TestThreadGroup(numIterations -> new Reader(numIterations, ringBuffer));
        group.start(null);
        group.waitForCompletion(profiler);
        return group.getReaderSum();
    }

    static long runAsync(int numIterations, Queue<Event> ringBuffer, Profiler profiler) {
        Reader reader = new Reader(numIterations, ringBuffer);
        reader.startNow(null);
        reader.waitForCompletion(profiler);
        return reader.getSum();
    }

    private long sum;

    Reader(int numIterations, Queue<Event> ringBuffer) {
        super(numIterations, ringBuffer);
    }

    @Override
    public long getSum() {
        return sum;
    }

    @Override
    protected void loop() {
        Queue<Event> queue = getQueue();
        long sum = 0L;
        for (int numIterations = getNumIterations(); numIterations > 0; numIterations--) {
            Event element;
            while ((element = queue.poll()) == null) {
                Thread.onSpinWait();
            }
            sum += element.getData();
        }
        this.sum = sum;
    }
}
