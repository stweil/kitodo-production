/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.production.helper.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class RequestScopeCacheHelperTest {

    private MockedStatic<FacesContext> facesContextMockedStatic;

    @AfterEach
    public void tearDown() {
        if (facesContextMockedStatic != null) {
            facesContextMockedStatic.close();
        }
    }

    private void mockFacesContext() {
        FacesContext facesContext = Mockito.mock(FacesContext.class);
        ExternalContext externalContext = Mockito.mock(ExternalContext.class);
        Map<Object, Object> attributes = new HashMap<>();
        Mockito.when(facesContext.getAttributes()).thenReturn(attributes);
        Mockito.when(facesContext.getExternalContext()).thenReturn(externalContext);
        facesContextMockedStatic = Mockito.mockStatic(FacesContext.class);
        facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
    }

    @Test
    public void shouldCacheNonNullValueAndCallSupplierOnlyOnce() {
        mockFacesContext();
        AtomicInteger supplierCallCount = new AtomicInteger(0);

        for (int i = 0; i < 3; i++) {
            String value = RequestScopeCacheHelper.getFromCache("someKey", () -> {
                supplierCallCount.incrementAndGet();
                return "someValue";
            }, String.class);
            assertEquals("someValue", value);
        }
        assertEquals(1, supplierCallCount.get());
    }

    @Test
    public void shouldCacheNullValueWithoutThrowingAndCallSupplierOnlyOnce() {
        mockFacesContext();
        AtomicInteger supplierCallCount = new AtomicInteger(0);

        for (int i = 0; i < 3; i++) {
            String value = RequestScopeCacheHelper.getFromCache("someKey", () -> {
                supplierCallCount.incrementAndGet();
                return null;
            }, String.class);
            assertNull(value);
        }
        assertEquals(1, supplierCallCount.get());
    }

    @Test
    public void shouldAlwaysEvaluateSupplierOutsideOfFacesContext() {
        AtomicInteger supplierCallCount = new AtomicInteger(0);

        for (int i = 0; i < 3; i++) {
            String value = RequestScopeCacheHelper.getFromCache("someKey", () -> {
                supplierCallCount.incrementAndGet();
                return "someValue";
            }, String.class);
            assertEquals("someValue", value);
        }
        assertEquals(3, supplierCallCount.get());
    }
}
