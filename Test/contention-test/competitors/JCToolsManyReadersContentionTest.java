/*
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

import org.jctools.queues.SpmcArrayQueue;
import test.Profiler;
import test.object.Event;
import test.object.FastManyReadersContentionTest;

import java.util.Queue;

class JCToolsManyReadersContentionTest extends FastManyReadersContentionTest {
    static final Queue<Event> QUEUE = new SpmcArrayQueue<>(FAST_NOT_ONE_TO_ONE_SIZE);

    public static void main(String[] args) {
        new JCToolsManyReadersContentionTest().runBenchmark();
    }

    @Override
    protected long testSum() {
        Profiler profiler = createThroughputProfiler(TOTAL_ELEMENTS);
        Writer.startAsync(TOTAL_ELEMENTS, QUEUE, profiler);
        return Reader.runGroupAsync(QUEUE, profiler);
    }
}
