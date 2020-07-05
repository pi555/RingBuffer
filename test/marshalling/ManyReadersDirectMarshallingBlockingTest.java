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

package test.marshalling;

import test.Profiler;

class ManyReadersDirectMarshallingBlockingTest extends ManyReadersDirectMarshallingBlockingContentionTest {
    public static void main(String[] args) {
        new ManyReadersDirectMarshallingBlockingTest().runBenchmark();
    }

    @Override
    protected long testSum() {
        Profiler profiler = createThroughputProfiler(TOTAL_ELEMENTS);
        DirectWriter.runAsync(TOTAL_ELEMENTS, RING_BUFFER, profiler);
        return DirectReader.runGroupAsync(RING_BUFFER, profiler);
    }
}