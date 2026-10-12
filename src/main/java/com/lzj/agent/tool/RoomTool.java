package com.lzj.agent.tool;
import com.lzj.entity.Room;
import com.lzj.mapper.RoomMapper;
import com.mybatisflex.core.query.QueryChain;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import static com.lzj.entity.table.RoomTableDef.ROOM;

/** @author 周航宇 */
@Slf4j
@Component
public class RoomTool {

    @Resource
    private RoomMapper roomMapper;

    @Tool(name = "queryRoomDetail", description = "根据房间名称查询该房间的完整详细信息")
    public String queryRoomDetail(
            @ToolParam(description = "用户ID") String userId,
            @ToolParam(description = "房间名称") String roomName) {
        log.info("✅调用 queryRoomDetail，参数：userId={}, roomName={}", userId, roomName);

        // 查询房间详细信息
        Room room = QueryChain.of(roomMapper)
                .where(ROOM.ROOM_NAME.like(roomName))
                .and(ROOM.USER_ID.eq(userId))
                .one();
        
        // 处理查询结果
        String result = """
                名称：%s<br/>
                图标：%s<br/>
                简介：%s<br/>
                """.formatted(
                room.getRoomName(),
                room.getEmoji(),
                room.getSummary()
        );
        log.info("✅调用 queryRoomDetail，结果：result={}", result);
        return result;
    }
}
