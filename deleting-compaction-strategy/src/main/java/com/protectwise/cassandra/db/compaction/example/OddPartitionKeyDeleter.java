/*
 * Copyright 2016 ProtectWise, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.protectwise.cassandra.db.compaction.example;

import com.protectwise.cassandra.db.compaction.AbstractClusterDeletingConvictor;
import com.protectwise.cassandra.db.compaction.AbstractSimpleDeletingConvictor;
import com.protectwise.cassandra.util.PrintHelper;
import org.apache.cassandra.config.ColumnDefinition;
import org.apache.cassandra.cql3.ColumnIdentifier;
import org.apache.cassandra.db.BufferDecoratedKey;
import org.apache.cassandra.db.ColumnFamilyStore;
import org.apache.cassandra.db.Row;
import org.apache.cassandra.db.columniterator.OnDiskAtomIterator;
import org.apache.cassandra.db.composites.CellName;
import org.apache.cassandra.db.composites.CellNameType;
import org.apache.cassandra.db.composites.Composite;
import org.apache.cassandra.db.marshal.AbstractType;
import org.apache.cassandra.db.marshal.CompositeType;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OddPartitionKeyDeleter extends AbstractSimpleDeletingConvictor
{
	private static final Logger logger = LoggerFactory.getLogger(OddPartitionKeyDeleter.class);

	/**
	 * @param cfs
	 * @param options
	 */
	public OddPartitionKeyDeleter(ColumnFamilyStore cfs, Map<String, String> options)
	{
		super(cfs, options);
		logger.warn("You are using an example deleting compaction strategy.  Direct production use of these classes is STRONGLY DISCOURAGED!");
	}

	@Override
	public boolean shouldKeepPartition(OnDiskAtomIterator key)
	{
		ByteBuffer lastField = key.getKey().getKey();
		if (lastField.hasRemaining())
		{
			byte[] data = new byte[lastField.remaining()];
			lastField.duplicate().get(data);
			byte lastByte = data[data.length - 1];

			Map<ByteBuffer, ByteBuffer> pks = getNamedPkColumns(key);

			int a = ByteBufferUtil.toInt(pks.get(ByteBufferUtil.bytes("a")));

			return a % 2 == 0;
		}
		else
		{
			return true;
		}
	}


}
