<script setup>
import MapRoad from './MapRoad.vue';
import wnetwork from '@/wnetwork';
import { ref } from 'vue';
import LineMdAccountAlert from '~icons/line-md/account-alert?width=28px&height=28px';

var isShowMap = defineModel('isShowMap')
var mapData = ref([])

isShowMap.value = false
wnetwork.get("/api/map?id=" + sessionStorage.getItem("residenceId"))
    .then(response => {
        isShowMap.value = response.data.data == null
        mapData.value = response.data.data
    })
</script>

<template>
    <div class="map_container">
        <div class="map_view" v-if="isShowMap">
            <MapRoad v-for="(road, index) in mapData" :roadData="road" :ref="'maproad_' + index" />
        </div>
        <div class="map_nodata" v-else>
            <LineMdAccountAlert class="map_nodata_icon" />
            <div class="map_nodata_text">
                该宿舍暂无地图数据
            </div>
        </div>
    </div>
</template>

<script>
export default {
    methods: {
        setupData(washData){
            mapData.value.map((item, index) => {
                this.$refs['maproad_' + index][0].setupRoad(washData)
            })
        },
        refreshData(data) {
            mapData.value.map((item, index) => {
                this.$refs['maproad_' + index][0].refreshRoad(data)
            })
        }
    }
}
</script>

<style>
.map_nodata {
    display: flex;
}
</style>